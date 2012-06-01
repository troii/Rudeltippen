package controllers;

import java.util.List;

import models.User;
import play.db.jpa.Transactional;
import play.mvc.With;

@With(Auth.class)
@Transactional(readOnly=true)
public class Standings extends Root {
	public static void index() {
		final List<User> users = User.findAll();
		render(users);
	}
}