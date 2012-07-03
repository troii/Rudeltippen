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
import utils.ViewUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Overview extends Root{
	public static void index(int number, String page) {
		renderWrapper(number, page);
	}

	public static void playday(int number, String page) {
		renderWrapper(number, page);
	}

	public static void extra(String page) {
		final int number = 0;
		final Map pagination = ViewUtils.getPagination("user", page, "/overview/extra/");
		final List<User> users = User.find("ORDER BY place ASC").from((Integer) pagination.get("from")).fetch((Integer) pagination.get("fetch"));
		final List<Extra> extras = Extra.findAll();
		List<Map<User, List<ExtraTip>>> tips =  AppUtils.getExtraTips(users, extras);

		render(pagination, tips, extras, number);
	}
	
	private static void renderWrapper(int number, String page) {
		if (number <= 0) { number = 0; }
		List<Playday> playdays = Playday.findAll();
		Playday playday = Playday.find("byNumber", number).first();

		final Map pagination = ViewUtils.getPagination("user", page, "/overview/playday/");
		final List<User> users = User.find("ORDER BY place ASC").from((Integer) pagination.get("from")).fetch((Integer) pagination.get("fetch"));
		List<Map<User, List<GameTip>>> tips = AppUtils.getPlaydayTips(playday, users);

		render(playday, tips, playdays, number, pagination);
	}
}
