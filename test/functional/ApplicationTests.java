package functional;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

public class ApplicationTests extends FunctionalTest {
    @Test
    public void testApplicationIndex() {
        Response response = GET("/");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testApplicationRules() {
        Response response = GET("/application/rules");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

	@Test
	public void testAuthLogin() {
		Response response = GET("/auth/login");
		assertStatus(200, response);
	}

	@Test
	public void testAuthForgotten() {
		Response response = GET("/auth/forgotten");
		assertStatus(200, response);
	}

    @Test
    public void testAuthResend() {
        Response response = GET("/auth/resend");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/forgotten");
    }

    @Test
    public void testAuthLogout() {
        Response response = GET("/auth/logout");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileIndex() {
        Response response = GET("/users/show/user5");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileStatistic() {
        Response response = GET("/users/profile");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileUpdatenickname() {
        Response response = GET("/users/updatenickname");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileUpdatenotification() {
        Response response = GET("/users/updatenotifications");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileUpdatenpassword() {
        Response response = GET("/users/updatepassword");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileUpdatenpicture() {
        Response response = GET("/users/updatepicture");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testProfileUpdatenusername() {
        Response response = GET("/users/updateusername");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testTippsGames() {
        Response response = GET("/tips");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testTippsOverview() {
        Response response = GET("/overview");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testTippsStandings() {
        Response response = GET("/standings");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testUsersRegister() {
        Response response = GET("/auth/register");
        assertStatus(200, response);
    }

    @Test
    public void testUsersConfirm() {
        Response response = GET("/auth/confirm");
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/auth/login");
    }

    @Test
    public void testAuthenticatiedContent() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "user5@rudeltippen.de");
        params.put("userpass", "user5");
        Response response = POST("/auth/authenticate", params);
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/");

        assertStatus(200, GET("/"));
        assertStatus(200, GET("/application/rules"));

//TODO needs implementation
//
//        response = GET("/games/store");
//        assertStatus(302, GET("/games/store"));
//        assertEquals(response.getHeader("location"), "/games/results?playday=0");
//
//        assertStatus(200, GET("/profile/index"));
//
//        response = GET("/profile/statistic");
//        assertStatus(302, GET("/profile/statistic"));
//        assertEquals(response.getHeader("location"), "/");
//
//        assertStatus(200, GET("/profile/statistic/user5"));
//
//        response = GET("/profile/updatenickname");
//        assertStatus(302, GET("/profile/updatenickname"));
//        assertEquals(response.getHeader("location"), "/profile");
//
//        response = GET("/profile/updatenotifications");
//        assertStatus(302, GET("/profile/updatenotifications"));
//        assertEquals(response.getHeader("location"), "/profile");
//
//        response = GET("/profile/updatepassword");
//        assertStatus(302, GET("/profile/updatepassword"));
//        assertEquals(response.getHeader("location"), "/profile");
//
//        response = GET("/profile/updatepicture");
//        assertStatus(302, GET("/profile/updatepicture"));
//        assertEquals(response.getHeader("location"), "/profile");
//
//        response = GET("/profile/updateusername");
//        assertStatus(302, GET("/profile/updateusername"));
//        assertEquals(response.getHeader("location"), "/profile");
//
//        assertStatus(200, GET("/tipps/games"));
//
//        response = GET("/tipps/overview");
//        assertStatus(302, GET("/tipps/overview"));
//        assertEquals(response.getHeader("location"), "/tipps/overview?playday=0");
//
//        assertStatus(200, GET("/tipps/overview?playday=0"));
//
//        response = GET("/tipps/storebonus");
//        assertStatus(302, GET("/tipps/storebonus"));
//        assertEquals(response.getHeader("location"), "/tipps/games?playday=0");
//
//        response = GET("/tipps/storegames");
//        assertStatus(302, GET("/tipps/storegames"));
//        assertEquals(response.getHeader("location"), "/tipps/games?playday=0");
//
//        File file = new File(Play.applicationPath + "/conf/user.test.gif");
//        Map<String, File> files = new HashMap<String, File>();
//        files.put("picture", file);
//        response = POST("/profile/updatepicture", params, files);
//        assertEquals(response.getHeader("location"), "/profile");
//
//        User user = User.find("byUsername", "user5@rudeltippen.de").first();
//        assertNotNull(user.getPicture());
//        assertNotNull(user.getPictureLarge());
    }

    @Test
    public void testIsNotAdmin() {
//TODO needs implementation
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("username", "user8@rudeltippen.de");
//        params.put("userpass", "user8");
//        Response response = POST("/auth/authenticate", params);
//        assertStatus(302, response);
//        assertEquals(response.getHeader("location"), "/");
//
//        assertStatus(403, POST("/games/results"));
    }
}