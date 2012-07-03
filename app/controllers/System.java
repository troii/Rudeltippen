package controllers;

import interfaces.AppConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Game;
import models.Settings;
import models.User;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.Logger;
import play.Play;
import play.db.jpa.NoTransaction;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
import play.test.Fixtures;
import utils.AppUtils;
import utils.ValidationUtils;
import utils.ViewUtils;

public class System extends Controller implements AppConstants {
	@Before
	protected static void auth() {
		AppUtils.setAppLanguage();

		final String requestUsername = request.user;
		final String requestUserpass = request.password;
		final String appUsername = Play.configuration.getProperty("app.setup.username");
		final String appUserpass = Play.configuration.getProperty("app.setup.password");

		if (!appUsername.equals(requestUsername) || !appUserpass.equals(requestUserpass)) {
			unauthorized("Rudeltippen Setup");
		}
	}

	@Transactional(readOnly=true)
	public static void setup() {
		final Settings settings = AppUtils.getSettings();
		final List<String> timeZones = AppUtils.getTimezones();
		final List<String> locales = AppUtils.getLanguages();
		final List<String> themes = ViewUtils.getThemes();

		render(settings, timeZones, locales, themes);
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
							String tournament,
							boolean countFinalResult,
							boolean informOnNewTipper,
							boolean enableRegistration,
							String nickname,
							String username,
							String usernameConfirmation,
							String userpass,
							String userpassConfirmation,
							String theme
							) {
		if (AppUtils.verifyAuthenticity()) { checkAuthenticity(); }

		validation = ValidationUtils.getSettingsValidations(
				validation,
				tournament,
				usernameConfirmation,
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
				theme,
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

	        loadInitalData(tournament);

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
			settings.setTheme(theme);
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

	private static void loadInitalData(String tournament) {
		Fixtures.deleteAllModels();
		Fixtures.deleteDatabase();
		
		if (("em2012").equals(tournament)) {
			Fixtures.loadModels("bl2012.yml");
		} else if (("bl2012").equals(tournament)) {
			Fixtures.loadModels("em2012.yml");
		} else {
			Fixtures.loadModels("em2012.yml");
		}
	}
	
	@NoTransaction
	public static void yamler() {
		if (("true").equals(Play.configuration.getProperty("yamler"))) {
			List<String> playdays = generatePlaydays(34);
			List<String> games = getGamesFromWebService(34, "bl1", "2012");
			render(playdays, games);			
		}
		notFound();
	}

	private static List<String> getGamesFromWebService(int playdays, String leagueShortcut, String leagueSaison) {
		Map<String, String> teams = getBundesligaTeams();
		
		int game = 1;
		List<String> games = new ArrayList<String>();
		for (int k=1; k <= playdays; k++) {
			Document document = getDocumentFromWebService(String.valueOf(k), leagueShortcut, leagueSaison);
			NodeList nodeList = document.getElementsByTagName("Matchdata");
			for (int i=0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				NodeList childs = node.getChildNodes();
				
				String webserviceID = null;
				String kickoff = null;
				String homeTeam = null;
				String awayTeam = null;
				
				for (int j=0; j < childs.getLength(); j++) {
					Node childNode = childs.item(j);
					String name = childNode.getNodeName();
					String value = childNode.getTextContent();
					
					if ("matchID".equals(name)) {
						webserviceID = value;
					} else if (("matchDateTimeUTC").equals(name)) {
						value = value.replace("T", " ");
						value = value.replace("Z", "");
						kickoff = value;
						Logger.info("matchDateTimeUTC: " + value);
					} else if (("idTeam1").equals(name)) {
						homeTeam = teams.get(value);
					} else if (("idTeam2").equals(name)) {
						awayTeam = teams.get(value);
					}
				}
				
				games.add("models.Game(g" + game + "):<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;number:        " + game + "<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;homeTeam:      " + homeTeam + "<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;awayTeam:      " + awayTeam + "<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;kickoff:       " + kickoff + "<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;playday:       p" + k + "<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;playoff:       false<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;ended:         false<br />");
				games.add("&nbsp;&nbsp;&nbsp;&nbsp;webserviceID:  " + webserviceID + "<br />");
				games.add("<br />");
				game++;
			}	
		}
		
		return games;
	}

	private static Map<String, String> getBundesligaTeams() {
		Map<String, String> teams = new HashMap<String, String>();
		teams.put("7", "bvb");
		teams.put("134", "swb");
		teams.put("87", "bmg");
		teams.put("123", "tsg");
		teams.put("16", "vfb");
		teams.put("131", "vfl");
		teams.put("55", "h96");
		teams.put("9", "s04");
		teams.put("112", "scf");
		teams.put("81", "m05");
		teams.put("95", "fca");
		teams.put("185", "fd");
		teams.put("100", "hsv");
		teams.put("79", "fcn");
		teams.put("115", "sgf");
		teams.put("40", "fcb");
		teams.put("91", "ef");
		teams.put("6", "b04");
		
		return teams;
	}

    private static Document getDocumentFromWebService(String group, String leagueShortcut, String leagueSaison) {
        final String WS_ENCODING = "UTF-8";
        final String WS_CONTENT_TYPE = "application/soap+xml";
        final String WS_URL = "http://www.openligadb.de/Webservices/Sportsdata.asmx";

        StringBuilder buffer = new StringBuilder();
        buffer.append("<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">");
        buffer.append("<soap12:Body>");
        buffer.append("<GetMatchdataByGroupLeagueSaison xmlns=\"http://msiggi.de/Sportsdata/Webservices\">");
        buffer.append("<groupOrderID>" + group + "</groupOrderID>");
        buffer.append("<leagueShortcut>" + leagueShortcut + "</leagueShortcut>");
        buffer.append("<leagueSaison>" + leagueSaison + "</leagueSaison>");
        buffer.append("</GetMatchdataByGroupLeagueSaison>");
        buffer.append("</soap12:Body>");
        buffer.append("</soap12:Envelope>");

        Document document = null;
        try {
        	document = WS.url(WS_URL).setHeader("Content-Type", WS_CONTENT_TYPE).setHeader("charset", WS_ENCODING).body(buffer.toString()).post().getXml();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return document;
    }
	
	private static List<String> generatePlaydays(int count) {
		List<String> playdays = new ArrayList<String>();
		for (int i=1; i <= count; i++) {
			playdays.add("models.Playday(p" + i +"):<br />");
			playdays.add("&nbsp;&nbsp;&nbsp;&nbsp;name:          " + i + "spieltag<br />");
			playdays.add("&nbsp;&nbsp;&nbsp;&nbsp;current:       false<br />");
			playdays.add("&nbsp;&nbsp;&nbsp;&nbsp;playoff:       false<br />");
			playdays.add("&nbsp;&nbsp;&nbsp;&nbsp;number:        1<br />");
			playdays.add("<br />");
		}
		
		return playdays;
	}
}