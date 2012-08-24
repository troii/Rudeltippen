package unit;

import interfaces.AppConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jobs.CleanupJob;
import jobs.ReminderJob;
import jobs.ResultsJob;
import jobs.StandingsJob;
import jobs.UpdateJob;
import models.ConfirmationType;
import models.Game;
import models.Settings;
import models.Team;
import models.User;
import models.WSResult;
import models.WSResults;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import services.MailService;
import services.TwitterService;
import services.UpdateService;
import utils.AppUtils;
import utils.ValidationUtils;
import utils.ViewUtils;

public class UnitTests extends UnitTest implements AppConstants{
	@Before
	public void init() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("bl2012.test.yml");

		for (int i=1; i <= 7; i++) {
			final User user = new User();
			user.setAdmin(true);
			user.setUsername("user" + i + "@rudeltippen.de");
			user.setNickname("user" + i);
			user.setRegistered(new Date());
			user.setReminder(true);
			user.setActive(true);
			user.setSalt("foo");
			user.setUserpass(AppUtils.hashPassword("user" + i, "foo"));
			user._save();
		}
		User user = new User();
		user.setAdmin(false);
		user.setUsername("user8@rudeltippen.de");
		user.setNickname("user8");
		user.setRegistered(new Date());
		user.setReminder(true);
		user.setActive(true);
		user.setSalt("foo");
		user.setUserpass(AppUtils.hashPassword("user8", "foo"));
		user._save();

		user = new User();
		user.setAdmin(false);
		user.setUsername("user555@rudeltippen.de");
		user.setNickname("user555");
		user.setRegistered(new Date());
		user.setReminder(true);
		user.setActive(false);
		user.setSalt("foo");
		user.setUserpass(AppUtils.hashPassword("user555", "foo"));
		user._save();
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
	public void testIsValidEmail() {
		assertTrue(ValidationUtils.isValidEmail("sk@svenkubiak.de"));
		assertTrue(ValidationUtils.isValidEmail("peter.pong@plong.com"));
		assertTrue(ValidationUtils.isValidEmail("han.solo.senior@sub.domain.com"));
		assertFalse(ValidationUtils.isValidEmail("sk"));
		assertFalse(ValidationUtils.isValidEmail("sk@"));
		assertFalse(ValidationUtils.isValidEmail("@"));
		assertFalse(ValidationUtils.isValidEmail("@com.de"));
		assertFalse(ValidationUtils.isValidEmail("sk@.de"));
	}

	@Test
	public void testGeneratePassword() {
		assertEquals(AppUtils.hashPassword("user22", "foo"), "2d56a2593b5af39bb12082ad686b44bf9268c346");
	}

	@Test
	public void testGetWinnerLooser() {
		final Team home = new Team();
		final Team away = new Team();
		home.setName("home");
		away.setName("away");

		final Game game = new Game();
		game.setHomeTeam(home);
		game.setAwayTeam(away);
		game.setHomeScore("5");
		game.setAwayScore("3");

		assertEquals("home", game.getWinner().getName());
		assertEquals("away", game.getLoser().getName());

		game.setHomeScore("3");
		game.setAwayScore("5");

		assertEquals("home", game.getLoser().getName());
		assertEquals("away", game.getWinner().getName());

		game.setOvertime(true);
		game.setHomeScore("1");
		game.setAwayScore("1");
		game.setHomeScoreOT("8");
		game.setAwayScoreOT("3");

		assertEquals("home", game.getWinner().getName());
		assertEquals("away", game.getLoser().getName());

		game.setHomeScoreOT("3");
		game.setAwayScoreOT("8");

		assertEquals("home", game.getLoser().getName());
		assertEquals("away", game.getWinner().getName());
	}

	@Test
	public void testAppUtils() {
		assertNotNull(AppUtils.getTeamByReference("B-1-1"));
		assertNotNull(AppUtils.getTeamByReference("B-1-1"));
		assertTrue(!StringUtils.isNumeric(AppUtils.generatePassword(6)));
		assertNotNull(AppUtils.generatePassword(6));
		final Game g1 = new Game();
		final Game g2 = new Game();
		g1.setEnded(true);
		g2.setEnded(true);

		final List<Game> games = new ArrayList<Game>();
		games.add(g1);
		games.add(g2);

		assertTrue(AppUtils.allReferencedGamesEnded(games));

		g1.setEnded(false);

		assertFalse(AppUtils.allReferencedGamesEnded(games));

		assertTrue(AppUtils.getTimezones().size() > 0);
		assertTrue(AppUtils.getLanguages().size() > 0);

		final User user = new User();
		user.setPlace(1);
		user.setPreviousPlace(0);

		assertEquals("", ViewUtils.getPlaceTrend(user));

		user.setPlace(2);
		user.setPreviousPlace(1);

		assertEquals("<i class=\"icon-arrow-down icon-red\"></i> (1)", ViewUtils.getPlaceTrend(user));

		user.setPlace(1);
		user.setPreviousPlace(2);

		assertEquals("<i class=\"icon-arrow-up icon-green\"></i> (2)", ViewUtils.getPlaceTrend(user));

		user.setPreviousPlace(1);

		assertEquals("<i class=\"icon-minus\"></i> (1)", ViewUtils.getPlaceTrend(user));

		assertNotNull(AppUtils.getGravatarImage("sk@svenkubiak.de", null, PICTURESMALL));
		assertNotNull(AppUtils.getGravatarImage("sk@svenkubiak.de", null, PICTURELARGE));
		assertNotNull(AppUtils.getGravatarImage("sk@svenkubiak.de", null, -12));
		assertNotNull(AppUtils.getGravatarImage("sk@svenkubiak.de", null, 150));
		assertNotNull(AppUtils.getGravatarImage("bla@foobar5455fff.de", "mm", PICTURESMALL));
		assertNull(AppUtils.getGravatarImage("d@", null, PICTURESMALL));
	}

	@Test
	public void testValidationUtils() {
		final Settings settings = AppUtils.getSettings();
		final long maxSize = settings.getMaxPictureSize();


		assertTrue(ValidationUtils.isValidNickname("ahf_bA-SS747"));
		assertFalse(ValidationUtils.isValidNickname("ahf_bA-SS 747"));
		assertFalse(ValidationUtils.isValidNickname("ahf_bA-SS/747"));

		assertTrue(ValidationUtils.checkFileLength(maxSize));
		assertFalse(ValidationUtils.checkFileLength(maxSize + 1));
		assertTrue(ValidationUtils.usernameExists("user1@rudeltippen.de"));
		assertTrue(ValidationUtils.isValidEmail("user1@rudeltippen.de"));
		assertFalse(ValidationUtils.usernameExists("foobar555@bar.com"));
		assertTrue(ValidationUtils.nicknameExists("user5"));
		assertTrue(ValidationUtils.isValidScore("0", "0"));
		assertTrue(ValidationUtils.isValidScore("99", "99"));
		assertFalse(ValidationUtils.isValidScore("-1", "-1"));
		assertFalse(ValidationUtils.isValidScore("a", "b"));
		assertFalse(ValidationUtils.isValidScore("100", "1"));
		assertFalse(ValidationUtils.isValidScore("1", "100"));
		assertFalse(ValidationUtils.isValidScore("-1", "1"));
		assertFalse(ValidationUtils.isValidScore("1", "-51"));
	}

	@Test
	public void testViewUtils() {
		assertNotNull(ViewUtils.difference(new Date()));
		assertNotNull(ViewUtils.formatted(new Date()));
		assertEquals(ViewUtils.getPlaceName(-5), "");
		assertEquals(ViewUtils.getPlaceName(11), "");
		assertEquals(ViewUtils.getPlaceName(1), "Erster");
	}

	@Test
	public void testWebServiceUpdate() {
		final Game game = new Game();
		game.setWebserviceID("19218");
		final WSResults wsResults = UpdateService.getResultsFromWebService(game);
		final Map<String, WSResult> wsResult = wsResults.getWsResult();

		assertNotNull(wsResults);
		assertNotNull(wsResult);
		assertTrue(wsResult.containsKey("90"));
		assertTrue(wsResult.containsKey("120"));
		assertTrue(wsResult.containsKey("121"));
		assertEquals(wsResult.get("90").getHomeScore(), "0");
		assertEquals(wsResult.get("90").getAwayScore(), "0");
		assertEquals(wsResult.get("120").getHomeScore(), "0");
		assertEquals(wsResult.get("120").getAwayScore(), "0");
		assertEquals(wsResult.get("121").getHomeScore(), "3");
		assertEquals(wsResult.get("121").getAwayScore(), "4");
	}

	@Test
	public void testJobs() {
		new CleanupJob().now();
		new ReminderJob().now();
		new ResultsJob().now();
		new StandingsJob().now();
		new UpdateJob().now();
	}

	@Test
	public void testServices() {
		final User user = new User();
		final User admin = new User();

		user.setUsername("foo@bar.com");
		admin.setUsername("foo@bar.com");

		final String response = "foobar";
		final String message = "foobar";
		final String token = "foobar";
		final String userpass = "foobar";
		final String notification = "foobar";
		final ConfirmationType confirmationType = ConfirmationType.ACTIVATION;

		final List<String> statements = new ArrayList<String>();
		statements.add("foobar");

		MailService.confirm(user, token, confirmationType);
		MailService.newuser(user, admin);
		MailService.newuserpass(user, userpass);
		MailService.notifications(notification);
		MailService.updates(user, statements);
		MailService.webserviceError(response);
		TwitterService.updateStatus(message);
	}
}