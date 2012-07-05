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
        assertStatus(200, GET("/standings"));
        assertStatus(200, GET("/tips/extra"));
        assertStatus(200, GET("/tips/games/2"));
        assertStatus(200, GET("/users/show/user1"));
        assertStatus(200, GET("/users/profile"));
        assertStatus(200, GET("/tips/storeextra"));
        assertStatus(200, GET("/tips/storetips"));
        assertStatus(200, GET("/overview/1/1"));
        assertStatus(200, GET("/overview/extra"));
        assertStatus(200, GET("/admin/playday/1"));
        assertStatus(200, GET("/admin/users"));
        assertStatus(200, GET("/admin/settings"));
        assertStatus(200, GET("/admin/deleteuser/2"));
        assertStatus(302, GET("/admin/changeactive/3"));
        assertStatus(302, GET("/admin/changeadmin/3"));
        
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
        
    	response = GET("/api/standings");
        assertStatus(200, response);

        response = GET("/api/standings");
        assertStatus(200, response);
        
        response = GET("/api/tournament");
        assertStatus(200, response);
    
        response = GET("/api/user/user5");
        assertStatus(200, response);
        
        response = GET("/api/user/userfoo");
        assertStatus(501, response);
        
        response = GET("/api/playday/1");
        assertStatus(200, response);
        
        response = GET("/api/playday/4242");
        assertStatus(501, response);
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
    
    @Test
    public void testAPI() {
    	Response response = GET("/api/standings");
        assertStatus(401, response);

        response = GET("/api/standings");
        assertStatus(401, response);
        
        response = GET("/api/tournament");
        assertStatus(401, response);
    
        response = GET("/api/user/user2");
        assertStatus(401, response);
        
        response = GET("/api/user/userfoo");
        assertStatus(401, response);
        
        response = GET("/api/playday/1");
        assertStatus(401, response);
        
        response = GET("/api/playday/4242");
        assertStatus(401, response);        
    }
}