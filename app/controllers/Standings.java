package controllers;

import java.util.List;

import models.User;
import play.db.jpa.Transactional;
import play.mvc.With;

@With(Auth.class)
@Transactional(readOnly=true)
public class Standings extends Root {
	public static void index() {
		int start = 1;
		final List<User> users = User.find("ORDER BY place ASC").from(0).fetch(15);
		render(users, start);
	}
	
	public static void lazy(int start) {
		final List<User> users = User.find("ORDER BY place ASC").from(start).fetch(15);
		render(users, start);		
	}
}