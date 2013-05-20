package controllers;

import java.util.List;

import models.Playday;
import models.Settings;
import models.User;
import models.statistic.GameTipStatistic;
import play.db.jpa.Transactional;
import play.mvc.With;
import services.AppService;
import services.DataService;

@With(Auth.class)
@Transactional(readOnly=true)
public class Application extends Root {

    public static void index() {
        final int pointsDiff = AppService.getPointsToFirstPlace();
        final String diffToTop = AppService.getDiffToTop(pointsDiff);
        final Playday playday = AppService.getCurrentPlayday();
        final List<User> topUsers = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").fetch(5);
        final long users = User.count();

        render(topUsers, playday, users, diffToTop);
    }

    public static void rules() {
        final Settings settings = AppService.getSettings();
        render(settings);
    }

    public static void statistics() {
        final List<Object[]> games = DataService.getGameStatistics();
        final List<Object[]> results = DataService.getResultsStatistic();
        final List<GameTipStatistic> gameTipStatistics = GameTipStatistic.find("ORDER BY playday ASC").fetch();

        render(results, gameTipStatistics, games);
    }
}