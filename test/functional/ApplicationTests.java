package functional;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import models.User;

import org.junit.Test;

import play.Play;
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
        assertStatus(200, GET("/tips"));
        assertStatus(200, GET("/users/show/user1"));
        assertStatus(200, GET("/users/profile"));
        assertStatus(200, GET("/overview"));
        assertStatus(200, GET("/tips/storeextra"));
        assertStatus(200, GET("/tips/storetips"));

        response = GET("/users/updatenickname");
        assertStatus(302, GET("/users/updatenickname"));
        assertEquals(response.getHeader("location"), "/users/profile");
        
        response = GET("/users/updatenotifications");
        assertStatus(302, GET("/users/updatenotifications"));
        assertEquals(response.getHeader("location"), "/users/profile");

        response = GET("/users/updatepassword");
        assertStatus(302, GET("/users/updatepassword"));
        assertEquals(response.getHeader("location"), "/users/profile");

        response = GET("/users/updatepicture");
        assertStatus(302, GET("/users/updatepicture"));
        assertEquals(response.getHeader("location"), "/users/profile");

        response = GET("/users/updateusername");
        assertStatus(302, GET("/users/updateusername"));
        assertEquals(response.getHeader("location"), "/users/profile");
        
        response = GET("/users/updatepicture");
        assertStatus(302, GET("/users/updatepicture"));
        assertEquals(response.getHeader("location"), "/users/profile");
    }

    @Test
    public void testIsNotAdmin() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", "user8@rudeltippen.de");
        params.put("userpass", "user8");
        Response response = POST("/auth/authenticate", params);
        assertStatus(302, response);
        assertEquals(response.getHeader("location"), "/");

        assertStatus(403, POST("/admin/results"));
    }
}