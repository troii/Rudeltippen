package controllers;

import java.util.List;

import models.Bracket;
import models.Pagination;
import models.Playday;
import play.db.jpa.Transactional;
import play.mvc.With;
import utils.ViewUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Tournament extends Root {
	public static void brackets() {
		final List<Bracket> brackets = Bracket.findAll();
		render(brackets);
	}

	public static void playday(final long number) {
		final Pagination pagination = ViewUtils.getPagination(number, "/tournament/playday/");
		final Playday playday = Playday.find("byNumber", pagination.getNumberAsInt()).first();

		render(playday, pagination);
	}
}