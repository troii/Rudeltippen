package unit.models;

import java.util.Date;

import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;
import utils.AppUtils;

public class UserTests extends UnitTest {

    @Test
    public void testCreateUser() {
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
}