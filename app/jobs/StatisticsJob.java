package jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Game;
import models.GameTip;
import models.Playday;
import models.Settings;
import models.User;
import models.statistic.GameStatistic;
import models.statistic.GameTipStatistic;
import models.statistic.PlaydayStatistic;
import models.statistic.ResultStatistic;
import models.statistic.UserStatistic;
import play.Logger;
import play.jobs.On;
import utils.AppUtils;
import utils.DataUtils;

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
            final List<User> users = AppUtils.getAllActiveUsers();
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
                    this.setGameStatistic(playday);
                }
            }

            for (final User user : users) {
                this.setResultStatistic(user);
            }

            Logger.info("Finished Job: StatisticsJob");
        }
    }

    private void setResultStatistic(final User user) {
        ResultStatistic.delete("user = ?", user);

        final Settings settings = AppUtils.getSettings();
        final List<GameTip> gameTips = GameTip.find("byUser", user).fetch();
        for (final GameTip gameTip : gameTips) {
            final Game game = gameTip.getGame();
            if ((game != null) && game.isEnded()) {
                final String score = gameTip.getHomeScore() + ":" + gameTip.getAwayScore();
                ResultStatistic resultStatistic = ResultStatistic.find("byUserAndResult", user, score).first();
                if (resultStatistic == null) {
                    resultStatistic = new ResultStatistic();
                    resultStatistic.setUser(user);
                    resultStatistic.setResult(score);
                }

                final int points = gameTip.getPoints();
                if (points == settings.getPointsTip()) {
                    resultStatistic.setCorrectTips( resultStatistic.getCorrectTips() + 1 );
                } else if (points == settings.getPointsTipDiff()) {
                    resultStatistic.setCorrectDiffs( resultStatistic.getCorrectDiffs() + 1 );
                } else if (points == settings.getPointsTipTrend()) {
                    resultStatistic.setCorrectTrends( resultStatistic.getCorrectTrends() + 1 );
                }

                resultStatistic._save();
            }
        }
    }

    private void setGameStatistic(final Playday playday) {
        final Map<String, Integer> scores = new HashMap<String, Integer>();
        final List<Game> games = playday.getGames();
        for (final Game game : games) {
            if ((game != null) && game.isEnded()) {
                final String score = game.getHomeScore() + ":" + game.getAwayScore();
                if (!scores.containsKey(score)) {
                    scores.put(score, 1);
                } else {
                    scores.put(score, scores.get(score) + 1);
                }
            }
        }

        for (final Entry entry : scores.entrySet()) {
            GameStatistic gameStatistic = GameStatistic.find("byPlaydayAndGameResult", playday, entry.getKey()).first();
            if (gameStatistic == null) {
                gameStatistic = new GameStatistic();
                gameStatistic.setPlayday(playday);
            }

            gameStatistic.setGameResult((String) entry.getKey());
            gameStatistic.setResultCount((Integer) entry.getValue());
            gameStatistic._save();
        }
    }

    private void setGameTipStatistics(final Playday playday) {
        GameTipStatistic gameTipStatistic = GameTipStatistic.find("byPlayday", playday).first();
        if (gameTipStatistic == null) {
            gameTipStatistic = new GameTipStatistic();
            gameTipStatistic.setPlayday(playday);
        }

        final Object [] statistics = DataUtils.getPlaydayStatistics(playday);
        if ((statistics != null) && (statistics.length == 5)) {
            gameTipStatistic.setPoints(((Long) statistics [0]).intValue());
            gameTipStatistic.setCorrectTips(((Long) statistics [1]).intValue());
            gameTipStatistic.setCorrectDiffs(((Long) statistics [2]).intValue());
            gameTipStatistic.setCorrectTrends(((Long) statistics [3]).intValue());
            gameTipStatistic.setAvgPoints(((Double) statistics [4]).intValue());
        }

        gameTipStatistic._save();
    }

    private void setAscendingPlaydayPoints(final Playday playday, final User user) {
        final UserStatistic userStatistic = UserStatistic.find("byPlaydayAndUser", playday, user).first();

        final Object [] statistics = DataUtils.getAscendingStatistics(playday, user);
        if ((statistics != null) && (statistics.length == 4)) {
            userStatistic.setPoints(((Long) statistics [0]).intValue());
            userStatistic.setCorrectTips(((Long) statistics [1]).intValue());
            userStatistic.setCorrectDiffs(((Long) statistics [2]).intValue());
            userStatistic.setCorrectTrends(((Long) statistics [3]).intValue());
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