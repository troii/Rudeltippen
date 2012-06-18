package unit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jobs.CleanupJob;
import jobs.ReminderJob;
import jobs.ResultsJob;
import jobs.TwitterJob;
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

public class UnitTests extends UnitTest {
	@Before
	public void init() {
    	Fixtures.deleteDatabase();
    	Fixtures.loadModels("em2012.test.yml");

    	for (int i=1; i <= 7; i++) {
    		User user = new User();
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
    	Settings settings = AppUtils.getSettings();
    	assertNotNull(settings);

    	int pointsWin = settings.getPointsGameWin();
    	int pointsDraw = settings.getPointsGameDraw();

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
    	Settings setting = AppUtils.getSettings();
    	int pointsTipp = setting.getPointsTip();
    	int pointsDiff = setting.getPointsTipDiff();
    	int pointsTrend = setting.getPointsTipTrend();

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
    	assertEquals(AppUtils.hashPassword("user22", "foo"), "e4c11ac853006b9633f99c6915a01af68fc383e9");
    }

    @Test
    public void testGetWinnerLooser() {
    	Team home = new Team();
    	Team away = new Team();
    	home.setName("home");
    	away.setName("away");

    	Game game = new Game();
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
    	Game g1 = new Game();
    	Game g2 = new Game();
    	g1.setEnded(true);
    	g2.setEnded(true);

    	List<Game> games = new ArrayList<Game>();
    	games.add(g1);
    	games.add(g2);

    	assertTrue(AppUtils.allReferencedGamesEnded(games));

    	g1.setEnded(false);

    	assertFalse(AppUtils.allReferencedGamesEnded(games));

    	assertTrue(AppUtils.getTimezones().size() > 0);
    	assertTrue(AppUtils.getLanguages().size() > 0);
    }

    @Test
    public void testValidationUtils() {
    	Settings settings = AppUtils.getSettings();
        long maxSize = settings.getMaxPictureSize();

        
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
        Game game = new Game();
        game.setWebserviceID("19218");
        WSResults wsResults = UpdateService.getResultsFromWebService(game);
        Map<String, WSResult> wsResult = wsResults.getWsResult();

        assertNotNull(wsResults);
        assertNotNull(wsResult);
        assertTrue(wsResult.containsKey("90"));
        assertTrue(wsResult.containsKey("120"));
        assertTrue(wsResult.containsKey("121"));
        assertEquals(wsResult.get("90").getHomeScore(), "5");
        assertEquals(wsResult.get("90").getAwayScore(), "4");
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
    	new TwitterJob().now();
    	new UpdateJob().now();
    }
    
    @Test
    public void testServices() {
    	User user = new User();
    	User admin = new User();
    	
    	user.setUsername("foo@bar.com");
    	admin.setUsername("foo@bar.com");
    	
    	String response = "foobar";
    	String message = "foobar";
    	String token = "foobar";
    	String userpass = "foobar";
    	String notification = "foobar";
    	ConfirmationType confirmationType = ConfirmationType.ACTIVATION;
    	
    	List<String> statements = new ArrayList<String>();
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