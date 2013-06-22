package unit.utils;

import static org.junit.Assert.*;

import java.util.Date;

import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

import utils.AppUtils;
import utils.ValidationUtils;

public class ValidationUtilsTests extends UnitTest {
	
    @Before
    public void testCreateTestUsers() {
        Fixtures.deleteDatabase();
        Fixtures.loadModels("bl2013.test.yml");

        for (int i=1; i <= 7; i++) {
            final User user = new User();
            user.setAdmin(true);
            user.setEmail("user" + i + "@rudeltippen.de");
            user.setUsername("user" + i);
            user.setRegistered(new Date());
            user.setReminder(true);
            user.setActive(true);
            user.setSalt("foo");
            user.setUserpass(AppUtils.hashPassword("user" + i, "foo"));
            user._save();
        }
        User user = new User();
        user.setAdmin(false);
        user.setEmail("user8@rudeltippen.de");
        user.setUsername("user8");
        user.setRegistered(new Date());
        user.setReminder(true);
        user.setActive(true);
        user.setSalt("foo");
        user.setUserpass(AppUtils.hashPassword("user8", "foo"));
        user._save();

        user = new User();
        user.setAdmin(false);
        user.setEmail("user555@rudeltippen.de");
        user.setUsername("user555");
        user.setRegistered(new Date());
        user.setReminder(true);
        user.setActive(false);
        user.setSalt("foo");
        user.setUserpass(AppUtils.hashPassword("user555", "foo"));
        user._save();
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
    public void testUsernameAndEmail() {
        assertTrue(ValidationUtils.isValidUsername("ahf_bA-SS747"));
        assertFalse(ValidationUtils.isValidUsername("ahf_bA-SS 747"));
        assertFalse(ValidationUtils.isValidUsername("ahf_bA-SS/747"));

        assertTrue(ValidationUtils.emailExists("user1@rudeltippen.de"));
        assertTrue(ValidationUtils.isValidEmail("user1@rudeltippen.de"));
        assertFalse(ValidationUtils.emailExists("foobar555@bar.com"));
        assertTrue(ValidationUtils.usernameExists("user5"));
    }
    
    @Test
    public void testChechFileLength() {
        final long maxSize = 102400;
        assertTrue(ValidationUtils.checkFileLength(maxSize));
        assertFalse(ValidationUtils.checkFileLength(maxSize + 1)); 	
    }
    
    @Test
    public void testValidScore() {
        assertTrue(ValidationUtils.isValidScore("0", "0"));
        assertTrue(ValidationUtils.isValidScore("99", "99"));
        assertFalse(ValidationUtils.isValidScore("-1", "-1"));
        assertFalse(ValidationUtils.isValidScore("a", "b"));
        assertFalse(ValidationUtils.isValidScore("100", "1"));
        assertFalse(ValidationUtils.isValidScore("1", "100"));
        assertFalse(ValidationUtils.isValidScore("-1", "1"));
        assertFalse(ValidationUtils.isValidScore("1", "-51"));
    }
}