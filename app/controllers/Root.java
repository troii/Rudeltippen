package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import services.AppService;

public class Root extends Controller {
	@Before
	protected static void init() {
		AppService.setAppLanguage();

		if (!AppService.rudeltippenIsInizialized()) {
			redirect("/system/setup");
		}

		final User connectedUser = AppService.getConnectedUser();
		if (connectedUser != null) {
			renderArgs.put("connectedUser", connectedUser);
		} else {
			renderArgs.put("connectedUser", null);
		}

		renderArgs.put("currentPlayday", AppService.getCurrentPlayday());
	}
}