package unit.utils;

import static org.junit.Assert.*;

import java.util.Date;

import models.User;

import org.junit.Test;

import play.test.UnitTest;

import utils.ViewUtils;

public class ViewUtilsTest extends UnitTest {

    @Test
    public void testGetPlaceTrend() {
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
    }

    @Test
    public void testGetPlaceName() {
        assertEquals(ViewUtils.getPlaceName(-5), "");
        assertEquals(ViewUtils.getPlaceName(11), "");
        assertEquals(ViewUtils.getPlaceName(1), "Erster");
    }
    
    @Test
    public void testFormatDate() {
        assertNotNull(ViewUtils.difference(new Date()));
        assertNotNull(ViewUtils.formatted(new Date()));    	
    }
}