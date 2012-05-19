package controllers;

import interfaces.CheckAccess;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.Game;
import models.Playday;
import models.Settings;

import org.apache.commons.lang.StringUtils;

import play.cache.Cache;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.With;
import utils.AppUtils;

@With(Auth.class)
@CheckAccess("admin")
@Transactional(readOnly=true)
public class Admin extends Root {
	public static void index(int number) {
		if (number <= 0) { number = 1; }
		final List<Playday> playdays = Playday.findAll();
		final Playday playday = Playday.find("byNumber", number).first();

		render(playdays, playday, number);
	}

	public static void results(int number) {
		if (number <= 0) { number = 1; }
		final List<Playday> playdays = Playday.findAll();
		final Playday playday = Playday.find("byNumber", number).first();

		render(playdays, playday, number);
	}

	public static void storeresults() {
		final Map<String, String> map = params.allSimple();
		Set<String> keys = new HashSet<String>();
		for (Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			if (StringUtils.isNotBlank(key) && key.contains("game_") && (key.contains("_homeScore") || key.contains("_awayScore"))) {
				key = key.replace("game_", "");
				key = key.replace("_homeScore", "");
				key = key.replace("_awayScore", "");
				key = key.replace("_homeScore_et", "");
				key = key.replace("_awayScore_et", "");
				key = key.trim();
				keys.add(key);
			}
		}

		String gamekey = null;
		for (String key : keys) {
			gamekey = key;
			final String homeScore = map.get("game_" + key + "_homeScore");
			final String awayScore = map.get("game_" + key + "_awayScore");
			final String extratime = map.get("extratime_" + key);
			final String homeScoreExtratime = map.get("game_" + key + "_homeScore_et");
			final String awayScoreExtratime = map.get("game_" + key + "_awayScore_et");
			AppUtils.setGameScore(key, homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
		}
		AppUtils.calculateScoresAndPoints();

		flash.put("infomessage", Messages.get("controller.games.tippsstored"));
		flash.keep();

		int playday = 1;
		if (keys != null && keys.size() >= 1) {
			if (StringUtils.isNotBlank(gamekey)) {
				Game game = Game.findById(new Long(gamekey));
				if (game != null && game.getPlayday() != null) {
					playday = game.getPlayday().getNumber();
				}
			}
		}

		redirect("/admin/index/" + playday);
	}

	public static void settings() {
		final Settings settings = AppUtils.getSettings();
		final List<String> timeZones = AppUtils.getTimezones();
		final List<String> locales = AppUtils.getLanguages();

		render(settings, timeZones, locales);
	}
	
	public static void storesettings() {
		Cache.delete("settings");
		settings();
	}	
}