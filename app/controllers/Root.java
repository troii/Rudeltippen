package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import utils.AppUtils;
import utils.ViewUtils;

public class Root extends Controller {
	@Before
    protected static void init() {
		AppUtils.setAppLanguage();
		
	    final User connectedUser = AppUtils.getConnectedUser();
	    if (connectedUser != null) {
	        renderArgs.put("connectedUser", connectedUser);
	    } else {
	    	renderArgs.put("connectedUser", null);
	    }
	    renderArgs.put("currentPlayday", AppUtils.getCurrentPlayday());
	    renderArgs.put("theme", ViewUtils.getTheme());
	}
}