package unit.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Extra;
import models.Game;
import models.Settings;

import org.junit.Test;

import play.test.UnitTest;
import utils.AppUtils;

public class AppUtilsTests extends UnitTest {

	@Test
	public void testGetSettings() {
		assertNotNull(AppUtils.getSettings());
	}

	@Test
	public void testHashPassword() {
		assertEquals(AppUtils.hashPassword("user22", "foo"), "bc423bd3c40919ed73f470d4182e2292f368607225f12f48599b9e9d2bea97586e0d930792e5381f1ebb75177abd75fc1a2ef879b2f275535d14e550c8d17b8b");
	}

	@Test
	public void testExtraTippable() {
		Extra extra = new Extra();
		extra.setEnding(new Date());

		Extra extra2 = new Extra();
		extra2.setEnding(new Date(1371732121344L));

		List<Extra> extras = new ArrayList<Extra>();
		extras.add(extra);
		extras.add(extra2);

		assertFalse(AppUtils.extrasTipable(extras));

		extra.setEnding(new Date(9371732121344L));
		extra2.setEnding(new Date(9371732121344L));

		extras = new ArrayList<Extra>();
		extras.add(extra);
		extras.add(extra2);

		assertTrue(AppUtils.extrasTipable(extras));
	}

	@Test
	public void testSetAppLanguage() {
		AppUtils.setAppLanguage();
	}

	@Test
	public void testIsJobInstance() {
		assertTrue(AppUtils.isJobInstance());
	}

	@Test
	public void testAllReferencedGamesEnded() {
		Game game = new Game();
		game.setEnded(true);

		Game game2 = new Game();
		game2.setEnded(false);

		List<Game> games = new ArrayList<Game>();
		games.add(game);
		games.add(game2);

		assertFalse(AppUtils.allReferencedGamesEnded(games));

		game2.setEnded(true);

		games = new ArrayList<Game>();
		games.add(game);
		games.add(game2);

		assertTrue(AppUtils.allReferencedGamesEnded(games));
	}

	@Test
	public void testGetTippPoints() {
		final Settings setting = AppUtils.getSettings();
		final int pointsTipp = setting.getPointsTip();
		final int pointsDiff = setting.getPointsTipDiff();
		final int pointsTrend = setting.getPointsTipTrend();

		assertEquals(AppUtils.getTipPoints(1, 0, 1, 0), pointsTipp);
		assertEquals(AppUtils.getTipPoints(0, 1, 0, 1), pointsTipp);
		assertEquals(AppUtils.getTipPoints(1, 1, 1, 1), pointsTipp);
		assertEquals(AppUtils.getTipPoints(2, 0, 5, 3), pointsDiff);
		assertEquals(AppUtils.getTipPoints(0, 2, 3, 5), pointsDiff);
		assertEquals(AppUtils.getTipPoints(2, 2, 1, 1), pointsDiff);
		assertEquals(AppUtils.getTipPoints(1, 0, 4, 0), pointsTrend);
		assertEquals(AppUtils.getTipPoints(0, 1, 0, 4), pointsTrend);
		assertEquals(AppUtils.getTipPointsTrend(1, 0, 3, 0), pointsTrend);
		assertEquals(AppUtils.getTipPointsTrend(4, 5, 3, 7), pointsTrend);
		assertEquals(AppUtils.getTipPointsTrend(1, 2, 2, 1), 0);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 1, 1), pointsTipp);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 0, 0), pointsDiff);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 1, 0), 0);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 4, 5, 0, 1), 0);

		setting.setCountFinalResult(true);
		setting._save();

		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 1, 1), 0);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 0, 0), 0);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 5, 4), pointsTipp);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 4, 5, 4, 5), pointsTipp);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 1, 0), pointsDiff);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 4, 5, 0, 1), pointsDiff);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 5, 4, 2, 0), pointsTrend);
		assertEquals(AppUtils.getTipPointsOvertime(1, 1, 4, 5, 0, 2), pointsTrend);

		setting.setCountFinalResult(false);
		setting._save();
	}

	@Test
	public void testGetPoints() {
		final Settings settings = AppUtils.getSettings();
		assertNotNull(settings);

		final int pointsWin = settings.getPointsGameWin();
		final int pointsDraw = settings.getPointsGameDraw();

		int [] points = AppUtils.getPoints(0, 0);

		assertEquals(points.length, 2);
		assertEquals(points[0], pointsDraw);
		assertEquals(points[1], pointsDraw);

		points = AppUtils.getPoints(1, 0);

		assertEquals(points.length, 2);
		assertEquals(points[0], pointsWin);
		assertEquals(points[1], 0);

		points = AppUtils.getPoints(0, 1);

		assertEquals(points.length, 2);
		assertEquals(points[0], 0);
		assertEquals(points[1], pointsWin);
	}

	@Test
	public void testGetMailTemplate() {
		assertNotNull(AppUtils.getMailTemplate("newuser"));
		assertNotNull(AppUtils.getMailTemplate("password"));
		assertNotNull(AppUtils.getMailTemplate("error"));
		assertNotNull(AppUtils.getMailTemplate("reminder"));
		assertNotNull(AppUtils.getMailTemplate("notifications"));
		assertNotNull(AppUtils.getMailTemplate("rudelmail"));
	}

	@Test
	public void testGetGravatarImage() {
		assertNotNull(AppUtils.getGravatarImage("sk@svenkubiak.de", null, 64));
	}

	@Test
	public void testGetDiffToTop() {
		assertNotNull(AppUtils.getDiffToTop(0));
		assertNotNull(AppUtils.getDiffToTop(1));
		assertNotNull(AppUtils.getDiffToTop(2));
	}

	@Test
	public void testGetCurrentTimezone() {
		assertNotNull(AppUtils.getCurrentTimeZone());
	}

	@Test
	public void testGetTeamByReference() {
		assertNotNull(AppUtils.getTeamByReference("B-1-1"));
		assertNotNull(AppUtils.getTeamByReference("B-1-1"));
	}
}