package jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Game;
import models.GameTip;
import models.GameTipStatistic;
import models.Playday;
import models.PlaydayStatistic;
import models.Settings;
import models.User;
import models.UserStatistic;
import play.Logger;
import play.db.jpa.JPA;
import play.jobs.On;
import utils.AppUtils;

@On("0 0 4 * * ?")
public class StatisticsJob extends AppJob {

    public StatisticsJob() {
        this.setDescription("All long taking calculations for user and playday statistics are executed in this job.");
        this.setExecuted("Runs daily at 04:00");
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            Logger.info("Started Job: StatisticsJob");

            final List<Playday> playdays = Playday.find("SELECT p FROM Playday p ORDER BY number ASC").fetch();
            final List<User> users = User.findAll();
            for (final Playday playday : playdays) {
                if (playday.allGamesEnded()) {
                    final Map<String, Integer> scores = this.getScores(playday);
                    this.setPlaydayStatistics(playday, scores);

                    for (final User user : users) {
                        this.setPlaydayPoints(playday, user);
                        this.setAscendingPlaydayPoints(playday, user);
                    }

                    this.setPlaydayPlaces(playday);
                    this.setGameTipStatistics(playday);
                }
            }

            Logger.info("Finished Job: StatisticsJob");
        }
    }

	private void setGameTipStatistics(Playday playday) {
		GameTipStatistic gameTipStatistic = GameTipStatistic.find("byPlayday", playday).first();
		if (gameTipStatistic == null) {
			gameTipStatistic = new GameTipStatistic();
			gameTipStatistic.setPlayday(playday);
		}

        Object result = JPA.em()
                .createQuery("SELECT " +
                		"SUM(playdayPoints) AS points, " +
                		"SUM(playdayCorrectTips) AS tips, " +
                		"SUM(playdayCorrectDiffs) AS diffs," +
                		"SUM(playdayCorrectTrends) AS trends, " +
                		"ROUND(AVG(playdayPoints)) AS avgPoints " +
                		"FROM UserStatistic u WHERE u.playday.id = :playdayID")
                .setParameter("playdayID", playday.getId())
                .getSingleResult();
        
        if (result != null) {
        	Object [] values = (Object[]) result;
        	gameTipStatistic.setPoints(((Long) values [0]).intValue());
        	gameTipStatistic.setCorrectTips(((Long) values [1]).intValue());
        	gameTipStatistic.setCorrectDiffs(((Long) values [2]).intValue());
        	gameTipStatistic.setCorrectTrends(((Long) values [3]).intValue());
        	gameTipStatistic.setAvgPoints(((Double) values [4]).intValue());
        }
		
		gameTipStatistic._save();
	}

	private void setAscendingPlaydayPoints(final Playday playday, final User user) {
        final UserStatistic userStatistic = UserStatistic.find("byPlaydayAndUser", playday, user).first();

        Object result = JPA.em()
                .createQuery(
                		"SELECT " +
                		"SUM(playdayPoints) AS points, " +
                		"SUM(playdayCorrectTips) AS correctTips, " +
                		"SUM(playdayCorrectDiffs) AS correctDiffs, " +
                		"SUM(playdayCorrectTrends) AS correctTrends " +
                		"FROM UserStatistic u " +
                		"WHERE u.playday.id <= :playdayID AND u.user.id = :userID")
                .setParameter("playdayID", playday.getId())
                .setParameter("userID", user.getId())
                .getSingleResult();

        if (result != null) {
        	Object [] values = (Object[]) result;
            userStatistic.setPoints(((Long) values [0]).intValue());
            userStatistic.setCorrectTips(((Long) values [1]).intValue());
            userStatistic.setCorrectDiffs(((Long) values [2]).intValue());
            userStatistic.setCorrectTrends(((Long) values [3]).intValue());
        }

        userStatistic._save();
    }

    private void setPlaydayPlaces(final Playday playday) {
        List<UserStatistic> userStatistics = UserStatistic.find("SELECT u FROM UserStatistic u WHERE playday = ? ORDER BY playdayPoints DESC", playday).fetch();
        int place = 1;
        for (final UserStatistic userStatistic : userStatistics) {
            userStatistic.setPlaydayPlace(place);
            userStatistic._save();
            place++;
        }

        userStatistics = UserStatistic.find("SELECT u FROM UserStatistic u WHERE playday = ? ORDER BY points DESC", playday).fetch();
        place = 1;
        for (final UserStatistic userStatistic : userStatistics) {
            userStatistic.setPlace(place);
            userStatistic._save();
            place++;
        }
    }

    private void setPlaydayPoints(final Playday playday, final User user) {
        int playdayPoints = 0;
        int correctTips = 0;
        int correctDiffs = 0;
        int correctTrends = 0;

        final Settings settings = AppUtils.getSettings();
        final List<Game> games = playday.getGames();
        for (final Game game : games) {
            final GameTip gameTip = GameTip.find("byUserAndGame", user, game).first();
            if (gameTip != null) {
                final int points = gameTip.getPoints();

                if (points == settings.getPointsTip()) {
                    correctTips++;
                } else if (points == settings.getPointsTipDiff()) {
                    correctDiffs++;
                } else if (points == settings.getPointsTipTrend()) {
                    correctTrends++;
                }
                playdayPoints = playdayPoints + points;
            }
        }

        UserStatistic userStatistic = UserStatistic.find("byUserAndPlayday", user, playday).first();
        if (userStatistic == null) {
            userStatistic = new UserStatistic();
            userStatistic.setPlayday(playday);
            userStatistic.setUser(user);
        }
        userStatistic.setPlaydayPoints(playdayPoints);
        userStatistic.setPlaydayCorrectTips(correctTips);
        userStatistic.setPlaydayCorrectDiffs(correctDiffs);
        userStatistic.setPlaydayCorrectTrends(correctTrends);
        userStatistic._save();
    }

    private Map<String, Integer> getScores(final Playday playday) {
        final Map<String, Integer> scores = new HashMap<String, Integer>();

        final List<Game> games = playday.getGames();
        for (final Game game : games) {
            final List<GameTip> gameTips = GameTip.find("byGame", game).fetch();
            for (final GameTip gameTip : gameTips) {
                final String score = gameTip.getHomeScore() + ":" + gameTip.getAwayScore();
                if (!scores.containsKey(score)) {
                    scores.put(score, 1);
                } else {
                    scores.put(score, scores.get(score) + 1);
                }
            }
        }
        return scores;
    }

    private void setPlaydayStatistics(final Playday playday, final Map<String, Integer> scores) {
        for (final Entry entry : scores.entrySet()) {
            PlaydayStatistic playdayStatistic = PlaydayStatistic.find("byPlaydayAndGameResult", playday, entry.getKey()).first();
            if (playdayStatistic == null) {
                playdayStatistic = new PlaydayStatistic();
                playdayStatistic.setPlayday(playday);
            }
            playdayStatistic.setGameResult((String) entry.getKey());
            playdayStatistic.setResultCount((Integer) entry.getValue());
            playdayStatistic._save();
        }
    }
}