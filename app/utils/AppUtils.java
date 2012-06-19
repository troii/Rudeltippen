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
import services.MailService;
import services.TwitterService;
import controllers.Auth.Security;

public class AppUtils implements AppConstants{
	/**
	 * Loads the currents settings from database
	 * @return Settings object
	 */
	public static Settings getSettings() {
		return Settings.find("byAppName", APPNAME).first();
	}

	/**
	 * Hashes a given clear-text password with a given salt using 100000 rounds
	 *
	 * @param userpass The password
	 * @param usersalt The salt
	 * @return SHA1 hashed string
	 */
    public static String hashPassword(String userpass, String usersalt) {
        final String salt = AppUtils.getSettings().getAppSalt();
        String password = userpass + salt + usersalt;
        for (int i = 0; i <= 100000; i++) {
            password = Codec.hexSHA1(password + salt + usersalt);
        }

        return password;
    }

    /**
     * Checks if at least one extra tip in given list is tipable
     *
     * @param extras List of extra tips
     * @return true if at least one extra tip is tipable, false otherwise
     */
    public static boolean extrasTipable(List<Extra> extras) {
		for (Extra extra : extras) {
			if (extra.isTipable()) {
				return true;
			}
		}

		return false;
    }

    /**
     * Sets the default application language defined in application.conf
     *
     * Default: en
     */
	public static void setAppLanguage() {
		String lang = Play.configuration.getProperty("default.language");
		if (StringUtils.isBlank(lang)) {
			lang = "en";
		}
		Lang.change(lang);
	}

	/**
	 * Finds the username in session and loads the user from the database
	*
	 * @return User object, null if not user is found
	 */
    public static User getConnectedUser() {
        final String username = Security.connected();
        User connectedUser = null;
        if (StringUtils.isNotBlank(username)) {
            connectedUser = User.find("byUsername", username).first();
        }

        return connectedUser;
    }

    /**
     * Generates a random string using RandomStringUtils.randomAlphanumeric
     *
     * @param length The length of the password, max. 30 letters
     * @return String with given length
     */
    public static String generatePassword(int length) {
        if (length <= 0 || length > 30) {
            length = 30;
        }

        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * Checks if the current application matches the defined job instance name
     *
     * @return true if current instance is job instance, false otherwise
     */
    public static boolean isJobInstance() {
        final String appName = Play.configuration.getProperty("application.name");
        final String jobInstance = Play.configuration.getProperty("app.jobinstance");
        if (StringUtils.isNotBlank(appName) && StringUtils.isNotBlank(jobInstance) && appName.equalsIgnoreCase(jobInstance)) {
            return true;
        }

        return false;
    }

    /**
     * Flushes the database, loads the test data and creates 100 users
     *
     * For TESTING purposes only!
     */
	public static void initApp() {
		Fixtures.deleteAllModels();
		Fixtures.deleteDatabase();
		Fixtures.loadModels("em2012.test.yml");

		String salt = "foo";
    	for (int i=1; i <= 100; i++) {
    		User user = new User();
    		user.setAdmin(true);
    		user.setUsername("user" + i + "@rudeltippen.de");
    		user.setNickname("user" + i);
    		user.setRegistered(new Date());
    		user.setActive(true);
    		user.setSalt(salt);
    		user.setUserpass(AppUtils.hashPassword("user" + i, salt));
    		user._save();
    	}
	}

	/**
	 * Calculates scores for all brackets and games as well as all user points (game tips and extra tips)
	 */
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

        setTeamPlaces();

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
        List<Game> allGames = Game.find("SELECT g FROM Game g WHERE ended = ?", true).fetch();
        for (User user : users) {
            int points = 0;
            int correctResults = 0;
            int correctDifferences = 0;
            int correctTrends = 0;
            int correctExtraTips = 0;

            for (Game game : allGames) {
                GameTip gameTip = GameTip.find("byUserAndGame", user, game).first();
                if (gameTip == null) {
                    continue;
                }

                int pointsForTipp = 0;
                if (game.isOvertime()) {
                    if (settings.isCountFinalResult()) {
                        pointsForTipp = AppUtils.getTipPoints(Integer.parseInt(game.getHomeScoreOT()), Integer.parseInt(game.getAwayScoreOT()), gameTip.getHomeScore(), gameTip.getAwayScore());
                    } else {
                        pointsForTipp = AppUtils.getTipPointsTrend(Integer.parseInt(game.getHomeScoreOT()), Integer.parseInt(game.getAwayScoreOT()), gameTip.getHomeScore(), gameTip.getAwayScore());
                    }
                } else {
                    pointsForTipp = AppUtils.getTipPoints(Integer.parseInt(game.getHomeScore()), Integer.parseInt(game.getAwayScore()), gameTip.getHomeScore(), gameTip.getAwayScore());
                }
                gameTip.setPoints(pointsForTipp);
                gameTip._save();
                
                if (pointsForTipp == settings.getPointsTip()) {
                    correctResults++;
                } else if (pointsForTipp == settings.getPointsTipDiff()) {
                    correctDifferences++;
                } else if (pointsForTipp == settings.getPointsTipTrend()) {
                    correctTrends++;
                }
                
                points = points + pointsForTipp;

            }
            user.setTipPoints(points);
            user.setCorrectResults(correctResults);
            user.setCorrectDifferences(correctDifferences);
            user.setCorrectTrends(correctTrends);

            int bonusPoints = 0;
            for (Extra extra : extras) {
                ExtraTip extraTip = ExtraTip.find("byUserAndExtra", user, extra).first();
                if (extraTip != null) {
                    Team bonusAnswer = extra.getAnswer();
                    Team userAnswer = extraTip.getAnswer();
                    if (bonusAnswer != null && userAnswer != null && bonusAnswer.equals(userAnswer)) {
                        int bPoints = extra.getPoints();
                        extraTip.setPoints(bPoints);
                        correctExtraTips++;
                        extraTip._save();
                        bonusPoints = bonusPoints + bPoints;
                    }
                }
            }
            
            user.setExtraPoints(bonusPoints);
            user.setPoints(points + bonusPoints);
            user.setCorrectExtraTips(correctExtraTips);
            user._save();
        }

        setUserPlaces();
        setPlayoffTeams(settings);
    }

	private static void setUserPlaces() {
		int i = 1;
		List<User> users = User.find("ORDER BY points DESC, correctResults DESC, correctDifferences DESC, correctTrends DESC, correctExtraTips DESC").fetch();
        for (User user : users) {
        	user.setPreviousPlace(user.getPlace());
            user.setPlace(i);
            user._save();
            i++;
        }
	}

	public static void setPlayoffTeams(final Settings settings) {
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

	private static void setTeamPlaces() {
    	List<Bracket> brackets = Bracket.find("byOverridePlaces", false).fetch();
    	for (Bracket bracket : brackets) {
    		List<Team> teams = Team.find("SELECT t FROM Team t WHERE bracket_id = ? ORDER BY points DESC, goalsDiff DESC, goalsFor DESC", bracket.getId()).fetch();
    		int place = 1;
    		for (Team team : teams) {
    			team.setPlace(place);
    			team._save();
    			place++;
    		}
    	}

    	for (Bracket bracket : brackets) {
    		List<Game> games = new ArrayList<Game>();
    		List<Team> teams = Team.find("SELECT t FROM Team t WHERE bracket_id = ? ORDER BY points DESC", bracket.getId()).fetch();
    		for (Team team : teams) {
    			Team aTeam = Team.find("SELECT t FROM Team t WHERE id != ? AND points = ? AND bracket_id = ?", team.getId(), team.getPoints(), bracket.getId()).first();
    			if (aTeam != null) {
        			Game game = Game.find("SELECT g FROM Game g WHERE homeTeam_id = ? AND awayTeam_id = ? AND playoff = ?", team.getId(), aTeam.getId(), false).first();
    				if (game != null) {
    					games.add(game);
    				}
        			game = Game.find("SELECT g FROM Game g WHERE homeTeam_id = ? AND awayTeam_id = ? AND playoff = ?", aTeam.getId(), team.getId(), false).first();
    				if (game != null) {
    					games.add(game);
    				}
    			}
    		}

    		for (Game game : games) {
				if (game != null) {
					Team homeTeam = game.getHomeTeam();
					Team awayTeam = game.getAwayTeam();
    				Team winner = game.getWinner();
    				if (winner != null) {
    					if (winner.equals(homeTeam) && homeTeam.getPlace() > awayTeam.getPlace()) {
    						int homeTeamPlace = homeTeam.getPlace();
    						int awayTeamPlace = awayTeam.getPlace();
    						homeTeam.setPlace(awayTeamPlace);
    						homeTeam._save();
    						awayTeam.setPlace(homeTeamPlace);
    						awayTeam._save();
    					} else if (winner.equals(awayTeam) && awayTeam.getPlace() > homeTeam.getPlace()) {
    						int homeTeamPlace = homeTeam.getPlace();
    						int awayTeamPlace = awayTeam.getPlace();
    						homeTeam.setPlace(awayTeamPlace);
    						homeTeam._save();
    						awayTeam.setPlace(homeTeamPlace);
    						awayTeam._save();
    					}
    				}
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

        boolean notify = false;
        if (!game.isEnded()) {
        	notify = true;
        }

        saveScore(game, homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);

        if (notify) {
            String notification = getNotificationMessage(game);
            TwitterService.updateStatus(notification);
            MailService.notifications(notification);
        }
    }

    /**
     * Generates a notifcation message for a given game
     *
     * @param game The game
     * @return The message
     */
	public static String getNotificationMessage(Game game) {
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

		return buffer.toString();
	}

	/**
	 * Checks if in current instance twitter configuration is enabled
	 *
	 * @return true if enabled, false otherwise
	 */
    public static boolean isTweetable() {
    	final String tweetable = Play.configuration.getProperty("twitter.enable");
    	if (StringUtils.isNotBlank(tweetable) && ("true").equals(tweetable)) {
    		return true;
    	}

    	return false;
    }

    /**
     * Checks if all games in given list have ended
     *
     * @param games The games to check
     * @return true if all games have ended, false otherweise
     */
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

	/**
	 * Loads a team from the database from a reference string
	 *
	 * e.g.:
	 * B-1-1  = Gets team from bracket 1 at place 1
	 * G-12-W = Gets the winner team from game 12
	 * G-12-L = Gets the looser team from game 12
	 *
	 * @param reference A string reference
	 * @return The team object
	 */
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
            if (aGame != null && aGame.isEnded()) {
                if ("W".equals(references[2])) {
                    team = aGame.getWinner();
                } else if (("L").equals(references[2])) {
                    team = aGame.getLoser();
                }
            }
        }

        return team;
    }

    /**
     * Returns the points for a given score and a given tip
     *
     * @param homeScore The score of the home team
     * @param awayScore The score of the away team
     * @param homeScoreTipp The tip for the score of the home team
     * @param awayScoreTipp The tip for the score of the away team
     * @return
     */
    public static int getTipPoints(int homeScore, int awayScore, int homeScoreTipp, int awayScoreTipp) {
        final Settings settings = AppUtils.getSettings();

        if (homeScore == homeScoreTipp && awayScore == awayScoreTipp) {
            return settings.getPointsTip();
        } else if ((homeScore - awayScore) == (homeScoreTipp - awayScoreTipp)) {
            return settings.getPointsTipDiff();
        } else if ((awayScore - homeScore) == (awayScoreTipp - homeScoreTipp)) {
        	return settings.getPointsTipDiff();
        }

        return getTipPointsTrend(homeScore, awayScore, homeScoreTipp, awayScoreTipp);
    }

    /**
     * Return the points for a trend if getTipPoints doenst find a previous match
     *
     * @param homeScore The score of the home team
     * @param awayScore The score of the away team
     * @param homeScoreTipp The tip for the score of the home team
     * @param awayScoreTipp The tip for the score of the away team
     * @return
     */
    public static int getTipPointsTrend(int homeScore, int awayScore, int homeScoreTipp, int awayScoreTipp) {
    	final Settings settings = AppUtils.getSettings();

        if ((homeScore > awayScore) && (homeScoreTipp > awayScoreTipp)) {
            return settings.getPointsTipTrend();
        } else if ((homeScore < awayScore) && (homeScoreTipp < awayScoreTipp)) {
        	return settings.getPointsTipTrend();
        }

        return 0;
    }

    /**
     * Saves a score to the database
     *
     * @param game The game object
     * @param homeScore The score of the home team
     * @param awayScore The score of the away team
     * @param extratime The name of the extratime
     * @param homeScoreExtratime The score of the home team in extratime
     * @param awayScoreExtratime The score of the away team in extratim
     */
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

	/**
	 * Calculates the points for the home and away team based on the score
	 *
	 * @param homeScore The score of the home team
	 * @param awayScore The score of the away team
	 * @return Array containing the points for the home team [0] and the away team [1]
	 */
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

    /**
     * Saves a tip for the currently connected user to the database
     *
     * @param game The game object
     * @param homeScore The score of the home team
     * @param awayScore The score of the away team
     */
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

    /**
     * Gathers the tips for a given playday for a given list of users
     *
     * @param playday The playday object
     * @param users List of users
     * @return All tips for the users and the playday
     */
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

	/**
	 * Gathers all extra tips for a given list of extra tips and a given user list
	 *
	 * @param users The list of user
	 * @param extras The list of extra tips
	 * @return All extra tips for the given users
	 */
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

	/**
	 * Returns the number of the playday where NOW() >= playdayStart AND NOW() <= playdayEnd
	 * @return The pladay number
	 */
	public static int getCurrentPlayday () {
		final Playday playday = Playday.find("SELECT p FROM Playday p WHERE NOW() >= playdayStart AND NOW() <= playdayEnd").first();
		if (playday != null && playday.getNumber() != 0) {
			return playday.getNumber();
		}

		return 0;
	}

	/**
	 * Gets all available timezones
	 * @return List of timezones
	 */
	public static List<String> getTimezones() {
		String [] zonesArray = TimeZone.getAvailableIDs();
		Arrays.sort(zonesArray);
		return Arrays.asList(zonesArray);
	}

	/**
	 * Gets all available languages
	 * @return List of languages
	 */
	public static List<String> getLanguages() {
		String [] localeArray = Locale.getISOLanguages();
		Arrays.sort(localeArray);
		return Arrays.asList(localeArray);
	}

	/**
	 * Parses a game from OpenLigaDB stores it in the database
	 *
	 * @param game The game object to store
	 * @param wsResults WSResults object containing the data from the webservice
	 */
    public static void setGameScoreFromWebService(Game game, final WSResults wsResults) {
        Map<String, WSResult> wsResult = wsResults.getWsResult();

        String homeScore = null;
        String awayScore = null;
        String homeScoreExtratime = null;
        String awayScoreExtratime = null;
        String extratime = null;

        if (wsResult.containsKey("90")) {
            homeScore = wsResult.get("90").getHomeScore();
            awayScore = wsResult.get("90").getAwayScore();
        }

        if (wsResult.containsKey("121")) {
            homeScoreExtratime = wsResult.get("121").getHomeScore();
            awayScoreExtratime = wsResult.get("121").getAwayScore();
            extratime = "ie";
        } else if (wsResult.containsKey("120")) {
            homeScoreExtratime = wsResult.get("120").getHomeScore();
            awayScoreExtratime = wsResult.get("120").getAwayScore();
            extratime = "nv";
        }

        Logger.info("Recieved from WebService - HomeScore: " + homeScore + " AwayScore: " + awayScore);
        Logger.info("Recieved from WebService - HomeScoreExtra: " + homeScoreExtratime + " AwayScoreExtra: " + awayScoreExtratime + " (" + extratime + ")");
        Logger.info("Updating results from WebService. " + game);
        setGameScore(String.valueOf(game.getId()), homeScore, awayScore, extratime, homeScoreExtratime, awayScoreExtratime);
        calculateScoresAndPoints();
    }

    /**
     * Checks if application uses the authenticity token
     *
     * @return true if check.authenticity is set in application.conf, false otherwise
     */
    public static boolean verifyAuthenticity() {
    	String check = Play.configuration.getProperty("check.authenticity");
    	if (!("false").equalsIgnoreCase(check)) {
    		return true;
    	}

    	return false;
    }

    /**
     * Checks if Rudeltippen update service is used or not
     * @return true if automatic.updates is set in application.conf, false otherwise
     */
    public static boolean automaticUpdates() {
    	String updates = Play.configuration.getProperty("automatic.updates");
    	if (("true").equalsIgnoreCase(updates)) {
    		return true;
    	}

    	return false;
    }

    /**
     * Returns the full localized path to a mail template
     *
     * @param name The name of the template
     * @return The full template name e.g. /services/MailServer/de/reminder.txt
     */
    public static String getMailTemplate(String name) {
    	String lang = Play.configuration.getProperty("default.language");
    	if (StringUtils.isBlank(lang)) {
    		lang = "en";
    	}

    	return "services/MailService/" + lang + "/" + name;
    }
}