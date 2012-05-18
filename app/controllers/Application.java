package controllers;

import java.util.List;

import models.Game;
import models.Settings;
import models.User;
import play.db.jpa.Transactional;
import play.mvc.With;
import utils.AppUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Application extends Root {
    public static void index() {
    	final List<User> topUsers = User.find("ORDER BY points DESC").fetch(3);
		final List<Game> nextGames = Game.find("SELECT g FROM Game g WHERE kickoff > NOW() AND homeTeam_id != null AND awayTeam_id != null ORDER BY kickoff ASC").fetch(3);
		final List<Game> previousGames = Game.find("SELECT g FROM Game g WHERE ended = 1 ORDER BY kickoff DESC").fetch(3);

		render(topUsers, nextGames, previousGames);
    }

	public static void rules() {
		Settings settings = AppUtils.getSettings();
		render(settings);
	}
}