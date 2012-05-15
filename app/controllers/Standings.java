package controllers;

import java.util.List;
import java.util.Map;

import models.User;
import play.mvc.With;
import utils.ViewUtils;

@With(Auth.class)
public class Standings extends Root {
	public static void index(String page) {
		final Map pagination = ViewUtils.getPagination("user", page, "");
		final List<User> users = User.find("ORDER BY points DESC").from((Integer) pagination.get("from")).fetch((Integer) pagination.get("fetch"));

		render(users, pagination);
	}
}
