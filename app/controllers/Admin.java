package controllers;

import interfaces.AppConstants;
import interfaces.CheckAccess;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.Confirmation;
import models.ConfirmationType;
import models.Game;
import models.Playday;
import models.Settings;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.With;
import utils.AppUtils;
import utils.ValidationUtils;

@With(Auth.class)
@CheckAccess("admin")
public class Admin extends Root implements AppConstants {
	@Transactional(readOnly=true)
	public static void index(int number) {
		if (number <= 0) { number = 1; }
		final List<Playday> playdays = Playday.findAll();
		final Playday playday = Playday.find("byNumber", number).first();

		render(playdays, playday, number);
	}

	@Transactional(readOnly=true)
	public static void results(int number) {
		if (number <= 0) { number = 1; }
		final List<Playday> playdays = Playday.findAll();
		final Playday playday = Playday.find("byNumber", number).first();

		render(playdays, playday, number);
	}

	@Transactional(readOnly=true)
	public static void users() {
		List<User> users = User.find("SELECT u FROM User u ORDER BY nickname ASC").fetch();
		render(users);
	}

	public static void storeresults() {
		if (AppUtils.verifyAuthenticity()) { checkAuthenticity(); }

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
				gamekey = gamekey.replace("_et", "");
				Game game = Game.findById(new Long(gamekey));
				if (game != null && game.getPlayday() != null) {
					playday = game.getPlayday().getNumber();
				}
			}
		}

		redirect("/admin/index/" + playday);
	}

	public static void updatesettings ( String name,
										int pointsGameWin,
										int pointsGameDraw,
										int pointsTip,
										int pointsTipDiff,
										int pointsTipTrend,
										int minutesBeforeTip,
										int maxPictureSize,
										String timeZoneString,
										String dateString,
										String dateTimeLang,
										String timeString,
										boolean countFinalResult,
										boolean informOnNewTipper,
										boolean enableRegistration
										) {
		if (AppUtils.verifyAuthenticity()) { checkAuthenticity(); }

		validation = ValidationUtils.getSettingsValidations(
				validation,
				name,
				pointsGameWin,
				pointsGameDraw,
				pointsTip,
				pointsTipDiff,
				pointsTipTrend,
				minutesBeforeTip,
				maxPictureSize,
				timeZoneString,
				dateString,
				dateTimeLang,
				timeString,
				countFinalResult,
				informOnNewTipper,
				enableRegistration);

		if (!validation.hasErrors()) {
			Settings settings = Settings.find("byAppName", APPNAME).first();
			settings.setName(name);
			settings.setPointsGameWin(pointsGameWin);
			settings.setPointsGameDraw(pointsGameDraw);
			settings.setPointsTip(pointsTip);
			settings.setPointsTipDiff(pointsTipDiff);
			settings.setPointsTipTrend(pointsTipTrend);
			settings.setMinutesBeforeTip(minutesBeforeTip);
			settings.setInformOnNewTipper(informOnNewTipper);
			settings.setTimeZoneString(timeZoneString);
			settings.setDateString(dateString);
			settings.setDateTimeLang(dateTimeLang);
			settings.setTimeString(timeString);
			settings.setCountFinalResult(countFinalResult);
			settings.setEnableRegistration(enableRegistration);
			settings.setMaxPictureSize(maxPictureSize);
			settings._save();

			flash.put("infomessage", Messages.get("setup.saved"));
	    	flash.keep();
		}
		params.flash();
		validation.keep();

		settings();
	}

	@Transactional(readOnly=true)
	public static void settings() {
		final Settings settings = AppUtils.getSettings();
		final List<String> timeZones = AppUtils.getTimezones();
		final List<String> locales = AppUtils.getLanguages();

		flash.put("name", settings.getName());
		flash.put("pointsGameWin", settings.getPointsGameWin());
		flash.put("pointsGameDraw", settings.getPointsGameDraw());
		flash.put("pointsTip", settings.getPointsTip());
		flash.put("pointsTipDiff", settings.getPointsTipDiff());
		flash.put("pointsTipTrend", settings.getPointsTipTrend());
		flash.put("minutesBeforeTip", settings.getMinutesBeforeTip());
		flash.put("informOnNewTipper", settings.isInformOnNewTipper());
		flash.put("timeZoneString", settings.getTimeZoneString());
		flash.put("dateString", settings.getDateString());
		flash.put("dateTimeLang", settings.getDateTimeLang());
		flash.put("timeString", settings.getTimeString());
		flash.put("countFinalResult", settings.isCountFinalResult());
		flash.put("enableRegistration", settings.isEnableRegistration());
		flash.put("maxPictureSize", settings.getMaxPictureSize());
		flash.put("enableRegistration", settings.isEnableRegistration());

		render(settings, timeZones, locales);
	}

	public static void changeactive(long userid) {
		final User connectedUser = AppUtils.getConnectedUser();
		User user = User.findById(userid);

		if (user != null) {
			if (!connectedUser.equals(user)) {
				String message;
				String activate;
				if (user.isActive()) {
					user.setActive(false);
					activate = "deactivated";
					message = Messages.get("info.change.deactivate", user.getUsername());
				} else {
					Confirmation confirmation = Confirmation.find("byConfirmationTypeAndUser", ConfirmationType.ACTIVATION, user).first();
					if (confirmation != null) {
						confirmation._delete();
					}
					user.setActive(true);
					activate = "activated";
					message = Messages.get("info.change.activate", user.getUsername());
				}
				user._save();
				flash.put("infomessage", message);
				Logger.info("User " + user.getUsername() + " has been " + activate + " - by " + connectedUser.getUsername());
			} else {
				flash.put("warningmessage", Messages.get("warning.change.active"));
			}
		} else {
			flash.put("errormessage", Messages.get("error.loading.user"));
		}

		flash.keep();
		redirect("/admin/users");
	}

	public static void changeadmin(long userid) {
		final User connectedUser = AppUtils.getConnectedUser();
		User user = User.findById(userid);

		if (user != null) {
			if (!connectedUser.equals(user)) {
				String message;
				String admin;
				if (user.isAdmin()) {
					message = Messages.get("info.change.deadmin", user.getUsername());
					admin = "is now admin";
					user.setAdmin(false);
				} else {
					message = Messages.get("info.change.admin", user.getUsername());
					admin = "is not admin anymore";
					user.setAdmin(true);
				}
				user._save();
				flash.put("infomessage", message);
				Logger.info("User " + user.getUsername() + " " + admin + " - by " + connectedUser.getUsername());
			} else {
				flash.put("warningmessage", Messages.get("warning.change.admin"));
			}
		} else {
			flash.put("errormessage", Messages.get("error.loading.user"));
		}

		flash.keep();
		redirect("/admin/users");
	}

	public static void deleteuser(long userid) {
		final User connectedUser = AppUtils.getConnectedUser();
		User user = User.findById(userid);

		if (user != null) {
			if (!connectedUser.equals(user)) {
				String username = user.getUsername();
				user._delete();
				flash.put("infomessage", Messages.get("info.delete.user", username));
				Logger.info("User " + username + " has been deleted - by " + connectedUser.getUsername());
				AppUtils.calculateScoresAndPoints();
			} else {
				flash.put("warningmessage", Messages.get("warning.delete.user"));
			}
		} else {
			flash.put("errormessage", Messages.get("error.loading.user"));
		}

		flash.keep();
		redirect("/admin/users");
	}
}