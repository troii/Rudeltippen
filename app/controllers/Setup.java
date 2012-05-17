package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.Settings;
import models.User;
import play.Play;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Controller;
import play.test.Fixtures;
import utils.AppUtils;

public class Setup extends Controller{
	@Before
	protected static void auth() {
		String requestUsername = request.user;
		String requestUserpass = request.password;
		String appUsername = Play.configuration.getProperty("app.setup.username");
		String appUserpass = Play.configuration.getProperty("app.setup.password");

		if (!appUsername.equals(requestUsername) || !appUserpass.equals(requestUserpass)) {
			unauthorized("Rudeltippen Setup");
		}
	}

	public static void index() {
		final Settings settings = AppUtils.getSettings();
		render(settings);
	}

	public static void init(String name,
							int pointsGameWin,
							int pointsGameDraw,
							int pointsTip,
							int pointsTipDiff,
							int pointsTipTrend,
							int minutesBeforeTip,
							String timeZoneString,
							String dateString,
							String dateTimeLang,
							String timeString,
							boolean countFinalResult,
							String bonusTipEnding,
							String nickname,
							String username,
							String usernameConfirmation,
							String userpass,
							String userpassConfirmation
							) {
		validation.required(name);
		validation.required(timeZoneString);
		validation.required(dateString);
		validation.required(dateTimeLang);
		validation.required(timeString);
		validation.required(username);
		validation.required(userpass);
		validation.required(nickname);
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

    	SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
    	Date extraEnding = null;
		try {
			extraEnding = df.parse(bonusTipEnding);
		} catch (ParseException e) {
			validation.isTrue(false).message(Messages.get("controller.setup.dateerror"));
		}

		if (!validation.hasErrors()) {
	    	session.clear();
	        response.removeCookie("rememberme");

			Fixtures.deleteDatabase();
	    	Fixtures.loadModels("em2012.yml");

			Settings settings = new Settings();
			settings.setAppSalt(Codec.hexSHA1(Codec.UUID()));
			settings.setAppName("rudeltippen");
			settings.setName(name);
			settings.setPointsGameWin(pointsGameWin);
			settings.setPointsGameDraw(pointsGameDraw);
			settings.setPointsTip(pointsTip);
			settings.setPointsTipDiff(pointsTipDiff);
			settings.setPointsTipTrend(pointsTipTrend);
			settings.setMinutesBeforeTip(minutesBeforeTip);
			settings.setBonusTippEnding(extraEnding);
			settings.setInformOnNewTipper(true);
			settings.setTimeZoneString(timeZoneString);
			settings.setDateString(dateString);
			settings.setDateTimeLang(dateTimeLang);
			settings.setTimeString(timeString);
			settings.setPlayoffs(true);
			settings.setPrePlayoffGames(24);
			settings.setCountFinalResult(countFinalResult);
			settings.setEnableRegistration(true);
			settings.setPlayoffTeams(2);
			settings.setMaxPictureSize(102400);
			settings._save();

			User user = new User();
			String salt = Codec.hexSHA1(Codec.UUID());
			user.setSalt(salt);
			user.setUsername(username);
			user.setNickname(nickname);
			user.setUserpass(AppUtils.hashPassword(userpass, salt));
			user.setRegistered(new Date());
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

		index();
	}
}