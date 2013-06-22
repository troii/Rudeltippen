package unit.models;

import static org.junit.Assert.*;
import models.Game;
import models.Team;

import org.junit.Test;

import play.test.UnitTest;

public class GameTests extends UnitTest {

	@Test
    public void testGetWinnerLooser() {
        final Team home = new Team();
        final Team away = new Team();
        home.setName("home");
        away.setName("away");

        final Game game = new Game();
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
}