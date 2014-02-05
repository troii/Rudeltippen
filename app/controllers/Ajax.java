package controllers;

import interfaces.CheckAccess;
import models.Game;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class)
@CheckAccess("admin")
public class Ajax extends Controller {
	public static void updatewebservice(final long gameid) {
		Game game = Game.findById(gameid);
		if (game != null) {
			final String webserviceID = params.get("value");
			if (StringUtils.isNotBlank(webserviceID)) {
				game.setWebserviceID(webserviceID);
				game._save();
				ok();
			}
		}
		badRequest();
	}
}