package jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import models.Game;
import models.GameTip;
import models.Playday;
import models.PlaydayStatistic;
import play.jobs.On;
import utils.AppUtils;

@On("0 0 4 * * ?")
public class StatisticsJob extends AppJob {

    public StatisticsJob() {
        this.setDescription("All long taking calculations for users and playday are executed in this job.");
        this.setExecuted("Runs daily at 04:00");
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            final List<Playday> playdays = Playday.findAll();
            for (final Playday playday : playdays) {
                if (playday.allGamesEnded()) {
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

                    for (final Entry entry : scores.entrySet()) {
                        PlaydayStatistic playdayStatistic = PlaydayStatistic.find("byPlayday", playday).first();
                        if (playdayStatistic == null) {
                            playdayStatistic = new PlaydayStatistic();
                            playdayStatistic.setPlayday(playday);
                        }
                        playdayStatistic.setGameResult((String) entry.getKey());
                        playdayStatistic.setResoultCount((int) entry.getValue());

                    }
                }
            }
        }
    }
}
