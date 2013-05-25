package controllers;

import java.util.List;
import java.util.Map;

import models.Extra;
import models.ExtraTip;
import models.GameTip;
import models.Pagination;
import models.Playday;
import models.User;
import play.db.jpa.Transactional;
import play.mvc.With;
import utils.AppUtils;
import utils.ViewUtils;

@With(Auth.class)
@Transactional(readOnly=true)
public class Overview extends Root{
    public static void playday(final long number) {
        final Pagination pagination = ViewUtils.getPagination(number, "/overview/playday/");

        final Playday playday = Playday.find("byNumber", pagination.getNumberAsInt()).first();
        final List<User> users = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").from(0).fetch(15);
        final List<Map<User, List<GameTip>>> tips = AppUtils.getPlaydayTips(playday, users);
        final long usersCount = User.count();

        render(playday, tips, pagination, usersCount);
    }

    public static void extras() {
        final List<User> users = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").fetch();
        final List<Extra> extras = Extra.findAll();
        final List<Map<User, List<ExtraTip>>> tips =  AppUtils.getExtraTips(users, extras);

        render(tips, extras);
    }

    public static void lazy(final int number, final int start) {
        final Playday playday = Playday.find("byNumber", number).first();
        final List<User> users = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").from(start).fetch(15);
        final List<Map<User, List<GameTip>>> tips = AppUtils.getPlaydayTips(playday, users);

        render(tips);
    }
}