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
import utils.AppUtils;
import utils.DataUtils;
import utils.ValidationUtils;

public class System extends Controller implements AppConstants {
	@Before
	protected static void auth() {
		AppUtils.setAppLanguage();

		final String requestUsername = request.user;
		final String requestUserpass = request.password;
		final String appUsername = Play.configuration.getProperty("app.setup.username");
		final String appUserpass = Play.configuration.getProperty("app.setup.password");

		if (AppUtils.appIsInizialized() && (!appUsername.equals(requestUsername) || !appUserpass.equals(requestUserpass))) {
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

	public static void init(final String name,
			final int pointsGameWin,
			final int pointsGameDraw,
			final int pointsTip,
			final int pointsTipDiff,
			final int pointsTipTrend,
			final int minutesBeforeTip,
			final int maxPictureSize,
			final boolean countFinalResult,
			final boolean informOnNewTipper,
			final boolean enableRegistration,
			final String nickname,
			final String username,
			final String usernameConfirmation,
			final String userpass,
			final String userpassConfirmation
			) {
		if (AppUtils.verifyAuthenticity()) { checkAuthenticity(); }

		validation = ValidationUtils.getSettingsValidations(
				validation,
				usernameConfirmation,
				pointsGameWin,
				pointsGameDraw,
				pointsTip,
				pointsTipDiff,
				pointsTipTrend,
				minutesBeforeTip,
				maxPictureSize,
				countFinalResult,
				informOnNewTipper,
				enableRegistration);
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

			DataUtils.loadInitalData();

			final List<Game> prePlayoffGames = Game.find("byPlayoff", false).fetch();
			final List<Game> playoffGames = Game.find("byPlayoff", true).fetch();
			boolean hasPlayoffs = false;
			if ((playoffGames != null) && (playoffGames.size() > 0)) {
				hasPlayoffs = true;
			}

			final Settings settings = new Settings();
			settings.setAppSalt(Codec.hexSHA1(Codec.UUID()));
			settings.setAppName(APPNAME);
			settings.setGameName(name);
			settings.setPointsGameWin(pointsGameWin);
			settings.setPointsGameDraw(pointsGameDraw);
			settings.setPointsTip(pointsTip);
			settings.setPointsTipDiff(pointsTipDiff);
			settings.setPointsTipTrend(pointsTipTrend);
			settings.setMinutesBeforeTip(minutesBeforeTip);
			settings.setInformOnNewTipper(informOnNewTipper);
			settings.setPlayoffs(hasPlayoffs);
			settings.setNumPrePlayoffGames(prePlayoffGames.size());
			settings.setCountFinalResult(countFinalResult);
			settings.setEnableRegistration(enableRegistration);
			settings.setMaxPictureSize(maxPictureSize);
			settings._save();

			final User user = new User();
			final String salt = Codec.hexSHA1(Codec.UUID());
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
			user.setCorrectResults(0);
			user.setCorrectDifferences(0);
			user.setCorrectTrends(0);
			user.setCorrectExtraTips(0);
			user._save();

			flash.put("infomessage", Messages.get("controller.setup.setup"));
			flash.keep();

			redirect("/auth/login");
		}
		params.flash();
		validation.keep();

		setup();
	}

	@NoTransaction
	public static void yamler() {
		if (("true").equals(Play.configuration.getProperty("yamler"))) {
			final List<String> playdays = DataUtils.generatePlaydays(34);
			final List<String> games = DataUtils.getGamesFromWebService(34, "bl1", "2012");
			render(playdays, games);
		}
		notFound();
	}
}