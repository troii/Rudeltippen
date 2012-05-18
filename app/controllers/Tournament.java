package controllers;

import java.util.List;

import models.Bracket;
import models.Playday;
import play.db.jpa.Transactional;
import play.mvc.With;

@With(Auth.class)
@Transactional(readOnly=true)
public class Tournament extends Root {
	public static void index() {
		final List<Bracket> brackets = Bracket.findAll();
		render(brackets);
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
}
