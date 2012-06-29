package controllers;

import java.util.List;

import models.Bracket;
import models.Playday;
import models.Settings;
import play.db.jpa.Transactional;
import play.mvc.With;
import utils.AppUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Tournament extends Root {
	public static void index() {
		Settings settings = AppUtils.getSettings();
		final boolean playoffs = settings.isPlayoffs();
		final List<Bracket> brackets = Bracket.findAll();
		final List<Playday> playdays = Playday.findAll();
		
		render(brackets, playdays, playoffs);
	}

	public static void brackets() {
		final List<Bracket> brackets = Bracket.findAll();
		render(brackets);
	}

	public static void preliminary() {
		final List<Playday> playdays = Playday.find("byPlayoff", false).fetch();
		render(playdays);
	}

	public static void playoffs() {
		final List<Playday> playdays = Playday.find("byPlayoff", true).fetch();
		render(playdays);
	}
	
	public static void playday(int number) {
		final Playday playday = Playday.find("byNumber", number).first();
		render(playday);
	}
}