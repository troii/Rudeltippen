package controllers;

import java.util.List;

import models.Playday;
import models.Settings;
import models.User;
import models.statistic.GameTipStatistic;
import play.db.jpa.Transactional;
import play.mvc.With;
import utils.AppUtils;
import utils.DataUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Application extends Root {

	public static void index() {
		final int pointsDiff = AppUtils.getPointsToFirstPlace();
		final String diffToTop = AppUtils.getDiffToTop(pointsDiff);
		final Playday playday = AppUtils.getCurrentPlayday();
		final List<User> topUsers = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").fetch(3);
		final long users = AppUtils.getAllActiveUsers().size();

		render(topUsers, playday, users, diffToTop);
	}

	public static void rules() {
		final Settings settings = AppUtils.getSettings();
		render(settings);
	}

	public static void statistics() {
		final List<Object[]> games = DataUtils.getGameStatistics();
		final List<Object[]> results = DataUtils.getResultsStatistic();
		final List<GameTipStatistic> gameTipStatistics = GameTipStatistic.find("ORDER BY playday ASC").fetch();

		render(results, gameTipStatistics, games);
	}
}