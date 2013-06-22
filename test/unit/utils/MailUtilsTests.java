	package unit.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import play.test.UnitTest;

import models.ConfirmationType;
import models.Extra;
import models.Game;
import models.User;
import utils.MailUtils;

public class MailUtilsTests extends UnitTest {

	@Test
	public void testReminder() {
		User user = new User();
		user.setEmail("sk@svenkubiak.de");
		
		List<Game> games = new ArrayList<Game>();
		List<Extra> extras = new ArrayList<Extra>();
		
		MailUtils.reminder(user, games, extras);
	}
	
	@Test
	public void testConfirm() {
		User user = new User();
		user.setEmail("sk@svenkubiak.de");
		
		MailUtils.confirm(user, "foo", ConfirmationType.ACTIVATION);
	}
	
	@Test
	public void testNewUser() {
		User user = new User();
		user.setEmail("sk@svenkubiak.de");
		
		User admin = new User();
		admin.setEmail("sk@svenkubiak.com");
		
		MailUtils.newuser(user, admin);
	}
	
	@Test
	public void testError() {
		MailUtils.error("foo", "sk@svenkubiak.de");
	}
	
	@Test
	public void testNotifications() {
		User user = new User();
		user.setEmail("sk@svenkubiak.de");
		
		MailUtils.notifications("foo","bar", user);
	}
	
	@Test
	public void testSendGameTips() {
		User user = new User();
		user.setEmail("sk@svenkubiak.de");
		
		List<Game> games = new ArrayList<Game>();
		
		MailUtils.sendGameTips(user, games);
	}
	
	@Test
	public void testSendRudelmail() {
		Object [] recipients = new Object [1];
		recipients[0] = "sk@svenkubiak.de";
		
		MailUtils.sendRudelmail("foo", "bar", recipients, "sk@svenkubiak.com");
	}
}