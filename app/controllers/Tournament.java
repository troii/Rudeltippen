package controllers;

import java.util.List;

import models.Bracket;
import models.Playday;
import play.db.jpa.Transactional;
import play.mvc.With;

@With(Auth.class)
@Transactional(readOnly=true)
public class Tournament extends Root {
	public static void brackets() {
		final List<Bracket> brackets = Bracket.findAll();
		render(brackets);
	}

	public static void playday(final int number) {
		final List<Playday> playdays = Playday.find("byPlayoff", false).fetch();
		final Playday playday = Playday.find("byNumber", number).first();
		final Playday currentPlayday = playday;
		final Playday nextPlayday = Playday.find("byNumber", currentPlayday.getNumber() + 1).first();
		final Playday previousPlayday = Playday.find("byNumber", currentPlayday.getNumber() - 1).first();

		render(playday, playdays, nextPlayday, previousPlayday);
	}
}