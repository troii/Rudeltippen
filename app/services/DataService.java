package services;

import java.util.List;

import models.Playday;
import models.User;
import play.db.jpa.JPA;

public class DataService {
	
	public static Object [] getPlaydayStatistics(Playday playday) {
		Object result = null;
		Object [] values = null;
		
		result = JPA.em()
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
			values = (Object[]) result;
		}
		
		return values;
	}
	
	public static List<Object[]> getGameStatistics() {
		List<Object []> results = JPA.em()
				.createQuery(
				"SELECT " +
				"SUM(resultCount) AS counts, " +
				"gameResult AS result " +
				"FROM GameStatistic g " +
				"GROUP BY gameResult " +
				"ORDER BY counts DESC").getResultList();
		
		return results;
	}
	
	public static Object []  getAscendingStatistics(final Playday playday, final User user) {
		Object result = null;
		Object [] values = null;
		
		result = JPA.em()
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
			values = (Object[]) result;
		}
		
		return values;
	}

	public static List<Object[]> getResultsStatistic() {
		List<Object []> results = JPA.em()
				.createQuery("SELECT " +
				"SUM(resultCount) AS counts, " +
				"gameResult AS result " +
				"FROM PlaydayStatistic p " +
				"GROUP BY gameResult " +
				"ORDER BY counts DESC").getResultList();
		
		return results;
	}
}