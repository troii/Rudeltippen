package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Extra;
import models.ExtraTip;
import models.Game;
import models.Playday;
import models.Team;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.With;
import utils.AppUtils;
import utils.ValidationUtils;

@With(Auth.class)
public class Tips extends Root {
	@Transactional(readOnly=true)
	public static void index(int number) {
		if (number <= 0) { number = 1; }
		List<Playday> playdays = Playday.findAll();
		Playday playday = Playday.find("byNumber", number).first();

		render(playdays, playday, number);
	}

	@Transactional(readOnly=true)
	public static void games(int number) {
		if (number <= 0) { number = 1; }
		Playday playday = Playday.find("byNumber", number).first();

		render(playday);
	}

	@Transactional(readOnly=true)
	public static void extra() {
		List<Extra> extras = Extra.findAll();
		boolean tippable = AppUtils.extrasTippable(extras);
		
		render(extras, tippable);
	}

	@Transactional(readOnly=true)
	public static void extras() {
		List<Playday> playdays = Playday.findAll();
		List<Extra> extras = Extra.findAll();
		boolean tippable = AppUtils.extrasTippable(extras);
		
		render(extras, playdays, tippable);
	}

	public static void storetips() {
		int tipped = 0;
		int playday = 1;
		List<String> keys = new ArrayList<String>();
		final Map<String, String> map = params.allSimple();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			if (StringUtils.isNotBlank(key) && key.contains("game_") && (key.contains("_homeScore") || key.contains("_awayScore"))) {
				key = key.replace("game_", "");
				key = key.replace("_awayScore", "");
				key = key.replace("_homeScore", "");
				key = key.trim();

				if (keys.contains(key)) {
					continue;
				}

				String homeScore = map.get("game_" + key + "_homeScore");
				String awayScore = map.get("game_" + key + "_awayScore");

				if (!ValidationUtils.isValidScore(homeScore, awayScore)) {
					continue;
				}

				Game game = Game.findById(Long.parseLong(key));
				if (game == null) {
					continue;
				}

				AppUtils.placeTip(game, Integer.parseInt(homeScore), Integer.parseInt(awayScore));
				keys.add(key);
				tipped++;

				playday = game.getPlayday().getNumber();
			}
		}
		if (tipped > 0) {
			flash.put("infomessage", Messages.get("controller.tipps.tippsstored"));
		} else {
			flash.put("warningmessage", Messages.get("controller.tipps.novalidtipps"));
		}
		flash.keep();

		redirect("/tips/index/" + playday);
	}

	public static void storeextratips() {
		final Map<String, String> map = params.allSimple();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();

			if (StringUtils.isNotBlank(key) && key.contains("bonus_") && key.contains("_teamId")) {
				final String teamdId = params.get(key);
				key = key.replace("bonus_", "");
				key = key.replace("_teamId", "");
				key = key.trim();

				String bId = key;
				String tId = teamdId;
				Long bonusTippId = null;
				Long teamId = null;

				if (StringUtils.isNotBlank(bId) && StringUtils.isNotBlank(tId)) {
					bonusTippId = Long.parseLong(bId);
					teamId = Long.parseLong(tId);
				} else {
					extras();
				}

				Extra extra = Extra.findById(bonusTippId);
				if (extra.isTippable()) {
					Team team = Team.findById(teamId);
					User user = AppUtils.getConnectedUser();
					if (team != null) {
						ExtraTip extraTip = ExtraTip.find("byUserAndExtra", user, extra).first();
						if (extraTip == null) {
							extraTip = new ExtraTip();
						}

						extraTip.setUser(user);
						extraTip.setExtra(extra);
						extraTip.setAnswer(team);
						extraTip._save();
						Logger.info("Stored extratip - " + user.getUsername() + " - " + extraTip);
					}
					flash.put("infomessage", Messages.get("controller.tipps.bonussaved"));
					flash.keep();
				}
			}
		}
		extras();
	}
}