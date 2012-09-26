package utils;

import interfaces.AppConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import models.Bracket;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Settings;
import models.Team;
import models.User;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.templates.JavaExtensions;

public class ViewUtils extends JavaExtensions implements AppConstants{

	public static String difference (final Date date) {
		final int MIN = 60;
		final int HOUR = MIN * 60;
		final int DAY = HOUR * 24;
		final int MONTH = DAY * 30;
		final int YEAR = DAY * 365;

		final Date now = new Date();
		String difference = null;
		if (date.after(now)) {
			final long delta = (date.getTime() - now.getTime()) / 1000;
			if (delta < 60) {
				difference = Messages.get("in.second" + pluralize(delta), delta);
			} else if (delta < HOUR) {
				final long minutes = delta / MIN;
				difference = Messages.get("in.minute" + pluralize(minutes), minutes);
			} else if (delta < DAY) {
				final long hours = delta / HOUR;
				difference = Messages.get("in.hour" + pluralize(hours), hours);
			} else if (delta < MONTH) {
				final long days = delta / DAY;
				difference = Messages.get("in.day" + pluralize(days), days);
			} else if (delta < YEAR) {
				final long months = delta / MONTH;
				difference = Messages.get("in.month" + pluralize(months), months);
			} else {
				final long years = delta / YEAR;
				difference = Messages.get("in.year" + pluralize(years), years);				
			}
		} else {
			difference = Messages.get("in.ended");
		}

		return difference;
	}

	public static String formatted (final Date date) {
		final Settings settings = AppUtils.getSettings();
		final String dateString = settings.getDateString();
		final String timeString = settings.getTimeString();

		String lang = Lang.get();
		if (StringUtils.isBlank(lang)) {
			lang = "de";
		}

		final Locale currentLocale = new Locale(lang, lang.toUpperCase());
		final SimpleDateFormat df = new SimpleDateFormat(dateString + " - " + timeString, currentLocale);

		return df.format(date);
	}

	public static String homeReferenceName (final Game game) {
		return getReference(game.getHomeReference());
	}

	public static String awayReferenceName (final Game game) {
		return getReference(game.getAwayReference());
	}

	public static String getGameTipAndPoints(final Game game) {
		String tip = "-";
		final User user = AppUtils.getConnectedUser();
		final GameTip gameTip = GameTip.find("byGameAndUser", game, user).first();

		if (gameTip != null) {
			if (gameTip.getGame() != null) {
				if (gameTip.getGame().isEnded()) {
					tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore() + " (" + gameTip.getPoints() + ")";
				} else {
					tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore();
				}
			}
		}

		return tip;
	}

	public static String getHomeScoreTip(final Game game) {
		String homeScore = "";
		final User user = AppUtils.getConnectedUser();
		final List<GameTip> gameTips = game.getGameTips();

		for (final GameTip gameTip : gameTips) {
			if (gameTip.getUser().equals(user)) {
				homeScore = String.valueOf(gameTip.getHomeScore());
				break;
			}
		}

		return homeScore;
	}

	public static String getAwayScoreTip(final Game game) {
		String awayScore = "";
		final User user = AppUtils.getConnectedUser();
		final List<GameTip> gameTips = game.getGameTips();

		for (final GameTip gameTip : gameTips) {
			if (gameTip.getUser().equals(user)) {
				awayScore = String.valueOf(gameTip.getAwayScore());
				break;
			}
		}

		return awayScore;
	}

	public static String getPoints(final Game game) {
		String points = "-";
		final User user = AppUtils.getConnectedUser();
		final List<GameTip> gameTips = game.getGameTips();

		for (final GameTip gameTip : gameTips) {
			if (gameTip.getGame().isEnded() && gameTip.getUser().equals(user)) {
				points = String.valueOf(gameTip.getPoints());
				break;
			}
		}

		return points;
	}

	public static String getTrend(final Game game) {
		String trend = "";
		if ((game == null) || game.getKickoff().after(new Date())) {
			trend = Messages.get("model.game.notenoughtipps");
		} else {
			final List<GameTip> gameTips = game.getGameTips();
			if ((gameTips == null) || (gameTips.size() < 4)) {
				trend = Messages.get("model.game.notenoughtipps");
			} else {
				int tipsHome = 0;
				int tipsDraw = 0;
				int tipsAway = 0;

				for (final GameTip gameTip : gameTips) {
					final int homeScore = gameTip.getHomeScore();
					final int awayScore = gameTip.getAwayScore();

					if (homeScore == awayScore) {
						tipsDraw++;
					} else if (homeScore > awayScore) {
						tipsHome++;
					} else if (homeScore < awayScore) {
						tipsAway++;
					}
				}

				trend = tipsHome + " / " + tipsDraw + " / " + tipsAway;
			}
		}

		return trend;
	}

	private static String getReference(final String reference) {
		final String [] references = reference.split("-");
		String message = "";
		if (("G").equals(references[0])) {
			if ("W".equals(references[2])) {
				message = Messages.get("model.game.winnergame") + " " + references[1];
			} else if (("L").equals(references[2])) {
				message = Messages.get("model.game.losergame") + " " + references[1];
			}
		} else if (("B").equals(references[0])) {
			final Bracket bracket = Bracket.find("byNumber", Integer.parseInt(references[1])).first();
			final String groupName = bracket.getName();
			final String placeName = getPlaceName(Integer.parseInt(references[2]));

			message = placeName + " " + Messages.get(groupName);
		}

		return message;
	}

	public static String getPlaceName(final int place) {
		String message = "";

		if (place == 1) {
			message = Messages.get("helper.first");
		} else if (place == 2){
			message = Messages.get("helper.second");
		} else if (place == 3){
			message = Messages.get("helper.third");
		} else if (place == 4){
			message = Messages.get("helper.fourth");
		} else if (place == 5){
			message = Messages.get("helper.fifth");
		} else if (place == 6){
			message = Messages.get("helper.six");
		} else if (place == 7){
			message = Messages.get("helper.seventh");
		} else if (place == 8){
			message = Messages.get("helper.eight");
		} else if (place == 9){
			message = Messages.get("helper.ninth");
		} else if (place == 10){
			message = Messages.get("helper.tenth");
		}

		return message;
	}

	public static Map getPagination(final String object, final String page, final String url) {
		final Map pagination = new HashMap();
		final int rowsPerPage = 10;
		final int offset = 5;
		int currentPage = 1;
		long rows = 0;

		if (StringUtils.isNotBlank(page)) {
			currentPage = Integer.parseInt(page);
		}

		if (("user").equals(object)) {
			rows = User.count();
		}

		if (rows > rowsPerPage) {
			pagination.put("showControls", true);
		} else {
			pagination.put("showControls", false);
		}

		if ((currentPage - 2) > 0) {
			pagination.put("offsetstart", currentPage - 2);
			pagination.put("offset", currentPage + 2);
		} else {
			pagination.put("offsetstart", 1);
			pagination.put("offset", offset);
		}
		pagination.put("pages", (int) Math.ceil(rows / rowsPerPage));
		pagination.put("currentPage", currentPage);
		pagination.put("from", (currentPage - 1) * rowsPerPage);
		pagination.put("fetch", rowsPerPage);
		pagination.put("url", url);

		return pagination;
	}

	public static String getResult(final Game game) {
		String result = "-";
		if (game.isEnded()) {
			if (game.isOvertime()) {
				result = game.getHomeScoreOT() + " : " + game.getAwayScoreOT() + " (" + Messages.get(game.getOvertimeType()) + ")";
			} else {
				result = game.getHomeScore() + " : " + game.getAwayScore();
			}
		}

		return result;
	}

	public static String getGameTipAndPoints(final GameTip gameTip) {
		String tip = "-";
		final Date date = new Date();

		final User user = AppUtils.getConnectedUser();
		if (gameTip != null) {
			final Game game = gameTip.getGame();
			if (game != null) {
				if (game.isEnded()) {
					tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore() + " (" + gameTip.getPoints() + ")";
				} else {
					if (date.after(game.getKickoff())) {
						tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore();
					} else {
						if (user.equals(gameTip.getUser())) {
							tip = gameTip.getHomeScore() + " : " + gameTip.getAwayScore();
						} else {
							tip = Messages.get("tiped");
						}
					}
				}
			}
		}

		return tip;
	}

	public static long getExtraTip(final Extra extra) {
		final User user = AppUtils.getConnectedUser();
		final ExtraTip extraTip = ExtraTip.find("byExtraAndUser", extra, user).first();
		long id = 0;

		if ((extraTip != null) && (extraTip.getAnswer() != null)) {
			id = extraTip.getAnswer().getId();
		}

		return id;
	}

	public static String getAnswer(final Extra extra) {
		final User user = AppUtils.getConnectedUser();
		final ExtraTip extraTip = ExtraTip.find("byExtraAndUser", extra, user).first();
		String answer = "";

		if ((extraTip != null) && (extraTip.getAnswer() != null)) {
			answer = Messages.get(extraTip.getAnswer().getName());
		}

		return answer;
	}

	public static String getExtraTipAnswer(final ExtraTip extraTip) {
		String answer = "-";
		if (extraTip.getAnswer() != null) {
			if (extraTip.getExtra().getEnding().getTime() < new Date().getTime()) {
				answer = Messages.get(extraTip.getAnswer().getName());
			} else {
				answer = Messages.get("model.user.tipped");
			}
		}

		return answer;
	}

	public static String getExtraTipPoints(final ExtraTip extraTip) {
		String points = "";
		if ((extraTip != null) && (extraTip.getExtra() != null) && (extraTip.getExtra().getAnswer() != null)) {
			points = " ("+ extraTip.getPoints() + ")";
		}

		return points;
	}

	public static String htmlUnescape(final String html) {
		return StringEscapeUtils.unescapeHtml(html);
	}

	public static String getPlaceTrend(final User user) {
		final int currentPlace = user.getPlace();
		final int previousPlace = user.getPreviousPlace();
		String trend = "";

		if (previousPlace > 0) {
			if (currentPlace < previousPlace) {
				trend = "<i class=\"icon-arrow-up icon-green\"></i>" + " (" + previousPlace + ")";
			} else if (currentPlace > previousPlace) {
				trend = "<i class=\"icon-arrow-down icon-red\"></i>" + " (" + previousPlace + ")";
			} else {
				trend = "<i class=\"icon-minus\"></i>" + " (" + previousPlace + ")";
			}
		}

		return trend;
	}

	public static String getPlaceTrend(final Team team) {
		final int currentPlace = team.getPlace();
		final int previousPlace = team.getPreviousPlace();
		String trend = "";

		if (previousPlace > 0) {
			if (currentPlace < previousPlace) {
				trend = "<i class=\"icon-arrow-up icon-green\"></i>" + " (" + previousPlace + ")";
			} else if (currentPlace > previousPlace) {
				trend = "<i class=\"icon-arrow-down icon-red\"></i>" + " (" + previousPlace + ")";
			} else {
				trend = "<i class=\"icon-minus\"></i>" + " (" + previousPlace + ")";
			}
		}

		return trend;
	}

	public static List<String> getThemes() {
		final List<String> themes = new ArrayList<String>();
		themes.add("amelia");
		themes.add("cerulean");
		themes.add("cyborg");
		themes.add("journal");
		themes.add("readable");
		themes.add("simplex");
		themes.add("slate");
		themes.add("spacelab");
		themes.add("spruce");
		themes.add("superhero");
		themes.add("united");

		return themes;
	}

	public static String getTheme() {
		final Settings settings = AppUtils.getSettings();
		String theme = "cerulean";
		if (settings != null) {
			theme = settings.getTheme();

			final File file = Play.getFile("/public/stylesheets/" + theme + ".css");
			if (!file.exists()) {
				theme = "cerulean";
			}
		}

		return theme;
	}

	public static String getScore(final Game game) {
		String score = "- : -";
		if (game.isEnded()) {
			if (game.isOvertime()) {
				score = game.getHomeScore() + " : " + game.getAwayScore() + " / " + game.getHomeScoreOT() + ":" + game.getAwayScoreOT() + " (" + game.getOvertimeType() + ")";
			} else {
				score = game.getHomeScore() + " : " + game.getAwayScore();
			}
		}

		return score;
	}

	public static String formatTimestamp(final Long timestamp) {
		final SimpleDateFormat df = new SimpleDateFormat(DEFAULT_TIMESTMAP);
		return df.format(new Date(timestamp));
	}
}