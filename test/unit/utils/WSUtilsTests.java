package unit.utils;

import static org.junit.Assert.*;

import java.util.Map;

import models.Game;
import models.WSResult;
import models.WSResults;

import org.junit.Test;

import play.test.UnitTest;

import utils.WSUtils;

public class WSUtilsTests extends UnitTest {

    @Test
    public void testWebServiceUpdate() {
        final Game game = new Game();
        game.setWebserviceID("19218");
        final WSResults wsResults = WSUtils.getResultsFromWebService(game);
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
}