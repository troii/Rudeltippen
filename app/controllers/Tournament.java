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
		final Settings settings = AppUtils.getSettings();
		final boolean playoffs = settings.isPlayoffs();
		final List<Bracket> brackets = Bracket.findAll();
		final List<Playday> playdays = Playday.findAll();
		final Playday currentPlayday = AppUtils.getCurrentPlayday();
		final Playday nextPlayday = Playday.find("byNumber", currentPlayday.getNumber() + 1).first();
		final Playday previousPlayday = Playday.find("byNumber", currentPlayday.getNumber() - 1).first();

		render(brackets, playdays, playoffs, currentPlayday, nextPlayday, previousPlayday);
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

	public static void playday(final int number) {
		playdayWrapper(number);
	}

	public static void playdayJS(final int number) {
		playdayWrapper(number);
	}

	private static void playdayWrapper(final int number) {
		final List<Playday> playdays = Playday.find("byPlayoff", false).fetch();
		final Playday playday = Playday.find("byNumber", number).first();
		final Playday currentPlayday = playday;
		final Playday nextPlayday = Playday.find("byNumber", currentPlayday.getNumber() + 1).first();
		final Playday previousPlayday = Playday.find("byNumber", currentPlayday.getNumber() - 1).first();

		render(playday, playdays, nextPlayday, previousPlayday);
	}
}