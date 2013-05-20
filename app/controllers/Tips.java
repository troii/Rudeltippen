package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Extra;
import models.Game;
import models.Pagination;
import models.Playday;
import models.Team;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.With;
import services.AppService;
import utils.ValidationUtils;
import utils.ViewUtils;

@With(Auth.class)
public class Tips extends Root {
    @Transactional(readOnly=true)
    public static void playday(final int number) {
        final Pagination pagination = ViewUtils.getPagination(number, "/tips/playday/");
        final Playday playday = Playday.find("byNumber", pagination.getNumberAsInt()).first();

        render(playday, number,pagination);
    }

    @Transactional(readOnly=true)
    public static void extras() {
        final List<Extra> extras = Extra.findAll();
        final boolean tippable = AppService.extrasTipable(extras);

        render(extras, tippable);
    }

    public static void storetips() {
        if (ValidationUtils.verifyAuthenticity()) { checkAuthenticity(); }

        int tipped = 0;
        int playday = 1;
        final List<String> keys = new ArrayList<String>();
        final Map<String, String> map = params.allSimple();
        for (final Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.isNotBlank(key) && key.contains("game_") && (key.contains("_homeScore") || key.contains("_awayScore"))) {
                key = key.replace("game_", "");
                key = key.replace("_awayScore", "");
                key = key.replace("_homeScore", "");
                key = key.trim();

                if (keys.contains(key)) {
                    continue;
                }

                final String homeScore = map.get("game_" + key + "_homeScore");
                final String awayScore = map.get("game_" + key + "_awayScore");

                if (!ValidationUtils.isValidScore(homeScore, awayScore)) {
                    continue;
                }

                final Game game = Game.findById(Long.parseLong(key));
                if (game == null) {
                    continue;
                }

                AppService.placeTip(game, Integer.parseInt(homeScore), Integer.parseInt(awayScore));
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

        redirect("/tips/playday/" + playday);
    }

    public static void storeextratips() {
        if (ValidationUtils.verifyAuthenticity()) { checkAuthenticity(); }

        final Map<String, String> map = params.allSimple();
        for (final Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();

            if (StringUtils.isNotBlank(key) && key.contains("bonus_") && key.contains("_teamId")) {
                final String teamdId = params.get(key);
                key = key.replace("bonus_", "");
                key = key.replace("_teamId", "");
                key = key.trim();

                final String bId = key;
                final String tId = teamdId;
                Long bonusTippId = null;
                Long teamId = null;

                if (StringUtils.isNotBlank(bId) && StringUtils.isNotBlank(tId)) {
                    bonusTippId = Long.parseLong(bId);
                    teamId = Long.parseLong(tId);
                } else {
                    extras();
                }

                final Extra extra = Extra.findById(bonusTippId);
                if (extra.isTipable()) {
                    final Team team = Team.findById(teamId);
                    AppService.placeExtraTip(extra, team);
                    flash.put("infomessage", Messages.get("controller.tipps.bonussaved"));
                    flash.keep();
                }
            }
        }
        extras();
    }

    public static void standings() {
        final List<User> users = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").fetch();
        render(users);
    }
}