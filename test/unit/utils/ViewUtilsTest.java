package unit.utils;

import models.User;
import org.junit.Test;
import play.test.UnitTest;
import utils.ViewUtils;

import java.util.Date;

public class ViewUtilsTest extends UnitTest {

    @Test
    public void testGetPlaceTrend() {
        final User user = new User();
        user.setPlace(1);
        user.setPreviousPlace(0);

        assertEquals("", ViewUtils.getPlaceTrend(user));

        user.setPlace(2);
        user.setPreviousPlace(1);

//        assertEquals("<span class=\"glyphicon glyphicon-arrow-down red\"></span> (1)", commonService.getPlaceTrend(user));

        user.setPlace(1);
        user.setPreviousPlace(2);

//        assertEquals("<span class=\"glyphicon glyphicon-arrow-up green\"></span> (2)", commonService.getPlaceTrend(user));

        user.setPreviousPlace(1);

//        assertEquals("<span class=\"glyphicon glyphicon-minus black\"></span> (1)", commonService.getPlaceTrend(user));
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