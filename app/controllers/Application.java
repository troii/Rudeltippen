package controllers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import models.Playday;
import models.Settings;
import models.User;
import play.db.jpa.JPA;
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
		final List<User> topUsers = User.find("ORDER BY place ASC").fetch(5);
		final long users = User.count();

		render(topUsers, playday, users, diffToTop);
	}

	public static void rules() {
		final Settings settings = AppUtils.getSettings();
		render(settings);
	}
	
	public static void statistics() {
        List<Object []> results = JPA.em().createQuery("SELECT SUM(p.resultCount) AS counts, p.gameResult AS result FROM PlaydayStatistic p GROUP BY p.gameResult ORDER BY counts DESC").getResultList();
        
		render(results);
	}
}