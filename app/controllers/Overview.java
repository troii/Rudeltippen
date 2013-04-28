package controllers;

import java.util.List;
import java.util.Map;

import models.Extra;
import models.ExtraTip;
import models.GameTip;
import models.Playday;
import models.User;
import play.db.jpa.Transactional;
import play.mvc.With;
import utils.AppUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Overview extends Root{
	public static void playday(final int number, final int page) {
		renderWrapper(number, page);
	}

	public static void extras(final String page) {
		final int number = 0;
		final List<User> users = User.find("ORDER BY place ASC").from(0).fetch(15);
		final List<Extra> extras = Extra.findAll();
		final List<Map<User, List<ExtraTip>>> tips =  AppUtils.getExtraTips(users, extras);

		render(tips, extras, number);
	}

	private static void renderWrapper(int number, final int page) {
		if (number <= 0) { number = 0; }
		final List<Playday> playdays = Playday.findAll();
		final Playday playday = Playday.find("byNumber", number).first();

		final Playday currentPlayday = playday;
		final Playday nextPlayday = Playday.find("byNumber", currentPlayday.getNumber() + 1).first();
		final Playday previousPlayday = Playday.find("byNumber", currentPlayday.getNumber() - 1).first();

		final List<User> users = User.find("ORDER BY place ASC").from(0).fetch(15);
		final List<Map<User, List<GameTip>>> tips = AppUtils.getPlaydayTips(playday, users);

		render(playday, tips, playdays, number, nextPlayday, previousPlayday);
	}
}