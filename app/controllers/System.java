package controllers;

import interfaces.AppConstants;

import java.util.Date;
import java.util.List;

import models.Game;
import models.Settings;
import models.User;
import play.Play;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Controller;
import play.test.Fixtures;
import utils.AppUtils;

public class System extends Controller implements AppConstants {
	@Before
	protected static void auth() {
		AppUtils.setAppLanguage();

		String requestUsername = request.user;
		String requestUserpass = request.password;
		String appUsername = Play.configuration.getProperty("app.setup.username");
		String appUserpass = Play.configuration.getProperty("app.setup.password");

		if (!appUsername.equals(requestUsername) || !appUserpass.equals(requestUserpass)) {
			unauthorized("Rudeltippen Setup");
		}
	}

	@Transactional(readOnly=true)
	public static void setup() {
		final Settings settings = AppUtils.getSettings();
		final List<String> timeZones = AppUtils.getTimezones();
		final List<String> locales = AppUtils.getLanguages();

		render(settings, timeZones, locales);
	}

	@NoTransaction
	public static void update() {
		render();
	}

	public static void init(String name,
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
							boolean enableRegistration,
							String nickname,
							String username,
							String usernameConfirmation,
							String userpass,
							String userpassConfirmation
							) {
		checkAuthenticity();
		validation.required(name);
		validation.required(timeZoneString);
		validation.required(dateString);
		validation.required(dateTimeLang);
		validation.required(timeString);
		validation.required(username);
		validation.required(userpass);
		validation.required(nickname);
		validation.range(pointsGameDraw, 0, 99);
		validation.range(pointsGameWin, 1, 99);
		validation.range(pointsGameDraw, 0, 99);
		validation.range(pointsTip, 0, 99);
		validation.range(pointsTipDiff, 0, 99);
		validation.range(pointsTipTrend, 0, 99);
		validation.range(minutesBeforeTip, 1, 1440);
		validation.email(username);
		validation.equals(username, usernameConfirmation);
		validation.equals(userpass, userpassConfirmation);
		validation.minSize(userpass, 8);
		validation.maxSize(userpass, 32);
		validation.minSize(nickname, 3);
		validation.maxSize(nickname, 20);

		if (!validation.hasErrors()) {
	    	session.clear();
	        response.removeCookie("rememberme");

	        Fixtures.deleteAllModels();
			Fixtures.deleteDatabase();
			//TODO: In future versions this should come from a dropdown in setup form
	    	Fixtures.loadModels("em2012.yml");

	    	List<Game> prePlayoffGames = Game.find("byPlayoff", false).fetch();
	    	List<Game> playoffGames = Game.find("byPlayoff", true).fetch();
	    	boolean hasPlayoffs = false;
	    	if (playoffGames != null && playoffGames.size() > 0) {
	    		hasPlayoffs = true;
	    	}

			Settings settings = new Settings();
			settings.setAppSalt(Codec.hexSHA1(Codec.UUID()));
			settings.setAppName(APPNAME);
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
			settings.setPlayoffs(hasPlayoffs);
			settings.setPrePlayoffGames(prePlayoffGames.size());
			settings.setCountFinalResult(countFinalResult);
			settings.setEnableRegistration(enableRegistration);
			settings.setMaxPictureSize(maxPictureSize);
			settings._save();

			User user = new User();
			String salt = Codec.hexSHA1(Codec.UUID());
			user.setSalt(salt);
			user.setUsername(username);
			user.setNickname(nickname);
			user.setUserpass(AppUtils.hashPassword(userpass, salt));
			user.setRegistered(new Date());
			user.setExtraPoints(0);
			user.setTipPoints(0);
			user.setPoints(0);
			user.setActive(true);
			user.setAdmin(true);
			user.setReminder(true);
			user._save();

			flash.put("infomessage", Messages.get("controller.setup.setup"));
	    	flash.keep();

			redirect("/auth/login");
		}
		params.flash();
		validation.keep();

		setup();
	}
}