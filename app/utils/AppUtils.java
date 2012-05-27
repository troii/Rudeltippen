package utils;

import interfaces.AppConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import models.Bracket;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.Playday;
import models.Settings;
import models.Team;
import models.User;
import models.WSResult;
import models.WSResults;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Codec;
import play.test.Fixtures;
import services.TwitterService;
import controllers.Auth.Security;

public class AppUtils implements AppConstants{
	public static Settings getSettings() {
		return Settings.find("byAppName", APPNAME).first();
	}

    public static String hashPassword(String userpass, String usersalt) {
        final String salt = AppUtils.getSettings().getAppSalt();
        String password = userpass + salt + usersalt;
        for (int i = 0; i <= 100000; i++) {
            password = Codec.hexSHA1(password + salt + usersalt);
        }

        return password;
    }

    public static boolean extrasTippable(List<Extra> extras) {
		for (Extra extra : extras) {
			if (extra.isTippable()) {
				return true;
			}
		}

		return false;
    }

	public static void setAppLanguage() {
		String defaultLanguage = Play.configuration.getProperty("default.language");
		if (StringUtils.isBlank(defaultLanguage)) {
			defaultLanguage = "de";
		}
		Lang.change(defaultLanguage);
	}

    public static User getConnectedUser() {
        final String username = Security.connected();
        User connectedUser = null;
        if (StringUtils.isNotBlank(username)) {
            connectedUser = User.find("byUsername", username).first();
        }

        return connectedUser;
    }

    public static String generatePassword(int length) {
        String password = RandomStringUtils.randomAlphanumeric(length);

        if (length <= 0 || length > 30) {
            password = RandomStringUtils.randomAlphanumeric(30);
        }

        return password;
    }

    public static boolean isJobInstance() {
        final String appName = Play.configuration.getProperty("application.name");
        final String jobInstance = Play.configuration.getProperty("app.jobinstance");
        if (StringUtils.isNotBlank(appName) && StringUtils.isNotBlank(jobInstance) && appName.equalsIgnoreCase(jobInstance)) {
            return true;
        }

        return false;
    }

	public static void initApp() {
		Fixtures.deleteAllModels();
		Fixtures.deleteDatabase();
		Fixtures.loadModels("em2012.test.yml");

    	for (int i=1; i <= 100; i++) {
    		User user = new User();
    		user.setAdmin(true);
    		user.setUsername("user" + i + "@rudeltippen.de");
    		user.setNickname("user" + i);
    		user.setRegistered(new Date());
    		user.setActive(true);
    		user.setSalt("foo");
    		user.setUserpass(AppUtils.hashPassword("user" + i, "foo"));
    		user._save();
    	}
	}

	public static void calculateScoresAndPoints() {
        final Settings settings = AppUtils.getSettings();
        final int pointsWin = settings.getPointsGameWin();
        final int pointsDraw = settings.getPointsGameDraw();

        final List<Team> teams = Team.findAll();
        for (Team team : teams) {
            final List<Game> homeGames = Game.find("byHomeTeam", team).fetch();
            final List<Game> awayGames = Game.find("byAwayTeam", team).fetch();

            int homePoints = 0;
            int awayPoints = 0;
            int gamesPlayed = 0;
            int gamesWon = 0;
            int gamesDraw = 0;
            int gamesLost = 0;
            int goalsFor = 0;
            int goalsAgainst = 0;
            for (Game game : homeGames) {
                if (!game.isPlayoff()) {
                    if (ValidationUtils.isValidScore(game.getHomeScore(), game.getAwayScore())) {
	                	int points = game.getHomePoints();
	                    homePoints = homePoints + points;
	                    gamesPlayed++;

	                    if (points == pointsWin) {
	                        gamesWon++;
	                    } else if (points == pointsDraw) {
	                        gamesDraw++;
	                    } else if (points == 0) {
	                        gamesLost++;
	                    }
                        goalsFor = goalsFor + Integer.parseInt(game.getHomeScore());
                        goalsAgainst = goalsAgainst + Integer.parseInt(game.getAwayScore());
                    }
                }
            }

            for (Game game : awayGames) {
                if (!game.isPlayoff()) {
                	 if (ValidationUtils.isValidScore(game.getHomeScore(), game.getAwayScore())) {
	                    int points = game.getAwayPoints();
	                    awayPoints = awayPoints + points;
	                    gamesPlayed++;

	                    if (points == pointsWin) {
	                        gamesWon++;
	                    } else if (points == pointsDraw) {
	                        gamesDraw++;
	                    } else if (points == 0) {
	                        gamesLost++;
	                    }
                        goalsFor = goalsFor + Integer.parseInt(game.getAwayScore());
                        goalsAgainst = goalsAgainst + Integer.parseInt(game.getHomeScore());
                    }
                }
            }
            team.setPoints(homePoints + awayPoints);
            team.setGamesDraw(gamesDraw);
            team.setGamesLost(gamesLost);
            team.setGamesWon(gamesWon);
            team.setGamesPlayed(gamesPlayed);
            team.setGoalsFor(goalsFor);
            team.setGoalsAgainst(goalsAgainst);
            team.setGoalsDiff(goalsFor - goalsAgainst);
            team._save();
        }

        List<Extra> extras = Extra.findAll();
        for (Extra extra : extras) {
            if (extra.getAnswer() == null) {
                if (AppUtils.allReferencedGamesEnded(extra.getGameReferences())) {
                    Team team = AppUtils.getTeamByReference(extra.getExtraReference());
                    if (team != null) {
                    	extra.setAnswer(team);
                    	extra._save();
                    }
                }
            }
        }

        List<User> users = User.findAll();
        for (User user : users) {
            List<Game> allGames = Game.find("SELECT g FROM Game g WHERE ended = ?", true).fetch();
            int points = 0;
            for (Game game : allGames) {
                GameTip gameTip = GameTip.find("byUserAndGame", user, game).first();
                if (gameTip == null) {
                    continue;
                }

                int pointsForTipp = 0;
                if (game.isOvertime()) {
                    if (settings.isCountFinalResult()) {
                        pointsForTipp = AppUtils.getTippPoints(Integer.parseInt(game.getHomeScoreOT()), Integer.parseInt(game.getAwayScoreOT()), gameTip.getHomeScore(), gameTip.getAwayScore());
                    } else {
                        pointsForTipp = AppUtils.getTippPointsTrend(Integer.parseInt(game.getHomeScoreOT()), Integer.parseInt(game.getAwayScoreOT()), gameTip.getHomeScore(), gameTip.getAwayScore());
                    }
                } else {
                    pointsForTipp = AppUtils.getTippPoints(Integer.parseInt(game.getHomeScore()), Integer.parseInt(game.getAwayScore()), gameTip.getHomeScore(), gameTip.getAwayScore());
                }
                gameTip.setPoints(pointsForTipp);
                gameTip._save();

                points = points + pointsForTipp;
            }
            user.setTipPoints(points);

            int bonusPoints = 0;
            for (Extra extra : extras) {
                ExtraTip extraTip = ExtraTip.find("byUserAndExtra", user, extra).first();
                if (extraTip != null) {
                    Team bonusAnswer = extra.getAnswer();
                    Team userAnswer = extraTip.getAnswer();
                    if (bonusAnswer != null && userAnswer != null && bonusAnswer.equals(userAnswer)) {
                        int bPoints = extra.getPoints();
                        extraTip.setPoints(bPoints);
                        extraTip._save();
                        bonusPoints = bonusPoints + bPoints;
                    }
                }
            }
            user.setExtraPoints(bonusPoints);
            user.setPoints(points + bonusPoints);
            user._save();
        }

        int i = 1;
        users = User.find("ORDER BY points DESC").fetch();
        for (User user : users) {
            user.setPlace(i);
            user._save();
            i++;
        }

        if (settings.isPlayoffs()) {
            List<Game> prePlayoffGames = Game.find("byPlayoffAndEnded", false, true).fetch();
            if (prePlayoffGames.size() == settings.getPrePlayoffGames()) {
                List<Game> playoffGames = Game.find("byPlayoffAndEnded", true, false).fetch();
                for (Game game : playoffGames) {
                    Team home = AppUtils.getTeamByReference(game.getHomeReference());
                    Team away = AppUtils.getTeamByReference(game.getAwayReference());
                    game.setHomeTeam(home);
                    game.setAwayTeam(away);
                    game._save();
                }
            }
        }
    }

    public static void setGameScore(String gameId, String homeScore, String awayScore, String extratime, String homeScoreExtratime, String awayScoreExtratime) {
        if (!ValidationUtils.isValidScore(homeScore, awayScore)) {
            return;
        }

        Game game = Game.findById(Long.parseLong(gameId));
        if (game == null) {
            return;
        }

        saveScore(game, homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);

        StringBuilder buffer = new StringBuilder();
    	buffer.append(Messages.get("helper.tweetscore"));
    	buffer.append(" ");
    	buffer.append(Messages.get(game.getHomeTeam().getName()));
    	buffer.append(" - ");
    	buffer.append(Messages.get(game.getAwayTeam().getName()));
    	buffer.append(" ");
        if (game.isOvertime()) {
        	buffer.append(game.getHomeScoreOT());
        	buffer.append(":");
        	buffer.append(game.getAwayScoreOT());
        	buffer.append(" (" + Messages.get(game.getOvertimeType()) + ")");
        } else {
        	buffer.append(game.getHomeScore());
        	buffer.append(":");
        	buffer.append(game.getAwayScore());
        }
        buffer.append(" - " + Messages.get(game.getPlayday().getName()));
        TwitterService.updateStatus(buffer.toString());
    }

    public static boolean isTweetable() {
    	String tweetable = Play.configuration.getProperty("twitter.enable");
    	if (StringUtils.isNotBlank(tweetable) && ("true").equals(tweetable)) {
    		return true;
    	}

    	return false;
    }

	public static boolean allReferencedGamesEnded(List<Game> games) {
        if (games == null || games.size() <= 0) {
            return false;
        }

        for (Game game : games) {
            if (!game.isEnded()) {
                return false;
            }
        }

        return true;
    }

    public static Team getTeamByReference(String reference) {
    	if (StringUtils.isBlank(reference)) {
    		return null;
    	}

    	String[] references = reference.split("-");
        if (references == null || references.length != 3) {
            return null;
        }

        Team team = null;
        if (("B").equals(references[0])) {
            Bracket bracket = Bracket.find("byNumber", Integer.parseInt(references[1])).first();
            if (bracket != null) {
                team = bracket.getTeamByPlace(Integer.parseInt(references[2]));
            }
        } else if (("G").equals(references[0])) {
            Game aGame = Game.find("byNumber", Integer.parseInt(references[1])).first();
            if (aGame != null) {
                if ("W".equals(references[2])) {
                    team = aGame.getWinner();
                } else if (("L").equals(references[2])) {
                    team = aGame.getLoser();
                }
            }
        }

        return team;
    }

    public static int getTippPoints(int homeScore, int awayScore, int homeScoreTipp, int awayScoreTipp) {
        final Settings settings = AppUtils.getSettings();

        if (homeScore == homeScoreTipp && awayScore == awayScoreTipp) {
            return settings.getPointsTip();
        } else if ((homeScore - awayScore) == (homeScoreTipp - awayScoreTipp)) {
            return settings.getPointsTipDiff();
        } else if ((awayScore - homeScore) == (awayScoreTipp - homeScoreTipp)) {
        	return settings.getPointsTipDiff();
        }

        return getTippPointsTrend(homeScore, awayScore, homeScoreTipp, awayScoreTipp);
    }

    public static int getTippPointsTrend(int homeScore, int awayScore, int homeScoreTipp, int awayScoreTipp) {
    	final Settings settings = AppUtils.getSettings();

        if ((homeScore > awayScore) && (homeScoreTipp > awayScoreTipp)) {
            return settings.getPointsTipTrend();
        } else if ((homeScore < awayScore) && (homeScoreTipp < awayScoreTipp)) {
        	return settings.getPointsTipTrend();
        }

        return 0;
    }

	private static void saveScore(Game game, String homeScore, String awayScore, String extratime, String homeScoreExtratime, String awayScoreExtratime) {
		int[] points = AppUtils.getPoints(Integer.parseInt(homeScore), Integer.parseInt(awayScore));
        game.setHomePoints(points[0]);
        game.setAwayPoints(points[1]);
        game.setHomeScore(homeScore);
        game.setAwayScore(awayScore);
        if (ValidationUtils.isValidScore(homeScoreExtratime, awayScoreExtratime )) {
            homeScoreExtratime = homeScoreExtratime.trim();
            awayScoreExtratime = awayScoreExtratime.trim();
            game.setOvertimeType(extratime);
            game.setHomeScoreOT(homeScoreExtratime);
            game.setAwayScoreOT(awayScoreExtratime);
            game.setOvertime(true);
        } else {
        	game.setOvertime(false);
        }
        game.setEnded(true);
        game._save();
	}

    public static int[] getPoints(int homeScore, int awayScore) {
        final Settings settings = AppUtils.getSettings();
        int[] points = new int[2];

        if (homeScore == awayScore) {
            points[0] = settings.getPointsGameDraw();
            points[1] = settings.getPointsGameDraw();
        } else if (homeScore > awayScore) {
            points[0] = settings.getPointsGameWin();
            points[1] = 0;
        } else if (homeScore < awayScore) {
            points[0] = 0;
            points[1] = settings.getPointsGameWin();
        }

        return points;
    }

    public static void placeTip(Game game, int homeScore, int awayScore) {
        final User user = AppUtils.getConnectedUser();
        GameTip gameTip = GameTip.find("byUserAndGame", user, game).first();
        if (game.isTippable() && ValidationUtils.isValidScore(String.valueOf(homeScore), String.valueOf(awayScore))) {
    		if (gameTip == null) {
    			gameTip = new GameTip();
    			gameTip.setGame(game);
    			gameTip.setUser(user);
    		}
    		gameTip.setPlaced(new Date());
    		gameTip.setHomeScore(homeScore);
    		gameTip.setAwayScore(awayScore);
    		gameTip._save();
            Logger.info("Tipp placed - " + user.getUsername() + " - " + gameTip);
        }
    }

	public static List<Map<User, List<GameTip>>> getPlaydayTips(Playday playday, List<User> users) {
		List<Map<User, List<GameTip>>> tips = new ArrayList<Map<User, List<GameTip>>>();

		for (User user : users) {
			Map<User, List<GameTip>> userTips = new HashMap<User, List<GameTip>>();
			List<GameTip> gameTips = new ArrayList<GameTip>();
			for (Game game : playday.getGames()) {
				GameTip gameTip = GameTip.find("byGameAndUser", game, user).first();
				if (gameTip == null) {
					gameTip = new GameTip();
				}
				gameTips.add(gameTip);
			}
			userTips.put(user,  gameTips);
			tips.add(userTips);
		}
		return tips;
	}

	public static List<Map<User, List<ExtraTip>>> getExtraTips(List<User> users, List<Extra> extras) {
		List<Map<User, List<ExtraTip>>> tips = new ArrayList<Map<User, List<ExtraTip>>>();

		for (User user : users) {
			Map<User, List<ExtraTip>> userTips = new HashMap<User, List<ExtraTip>>();
			List<ExtraTip> extraTips = new ArrayList<ExtraTip>();
			for (Extra extra : extras) {
				ExtraTip extraTip = ExtraTip.find("byExtraAndUser", extra, user).first();
				if (extraTip == null) {
					extraTip = new ExtraTip();
				}
				extraTips.add(extraTip);
			}
			userTips.put(user, extraTips);
			tips.add(userTips);
		}
		return tips;
	}

	public static int getCurrentPlayday () {
		final Playday playday = Playday.find("SELECT p FROM Playday p WHERE NOW() >= playdayStart AND NOW() <= playdayEnd").first();
		if (playday != null && playday.getNumber() != 0) {
			return playday.getNumber() - 1;
		}

		return 0;
	}

	public static List<String> getTimezones() {
		String [] zonesArray = TimeZone.getAvailableIDs();
		Arrays.sort(zonesArray);
		return Arrays.asList(zonesArray);
	}

	public static List<String> getLanguages() {
		String [] localeArray = Locale.getISOLanguages();
		Arrays.sort(localeArray);
		return Arrays.asList(localeArray);
	}

    public static void setGameScoreFromWebService(Game game, final WSResults wsResults) {
        Map<String, WSResult> wsResult = wsResults.getWsResult();
        final String homeScore = wsResult.get("90").getHomeScore();
        final String awayScore = wsResult.get("90").getAwayScore();
        String homeScoreExtratime = null;
        String awayScoreExtratime = null;
        String extratime = null;

        if (wsResult.containsKey("121")) {
            homeScoreExtratime = wsResult.get("121").getHomeScore();
            awayScoreExtratime = wsResult.get("121").getAwayScore();
            extratime = "ie";
        } else if (wsResult.containsKey("120")) {
            homeScoreExtratime = wsResult.get("120").getHomeScore();
            awayScoreExtratime = wsResult.get("120").getAwayScore();
            extratime = "nv";
        }

        Logger.info("Updating results from WebService. " + game);
        setGameScore(String.valueOf(game.getId()), homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
        calculateScoresAndPoints();
    }
}