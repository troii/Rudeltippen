package controllers;

import interfaces.CheckAccess;

import java.text.SimpleDateFormat;
import java.util.Locale;

import models.Bracket;
import models.Game;
import models.Team;

import org.apache.commons.lang.StringUtils;

import play.mvc.Controller;
import play.mvc.With;

@With(Auth.class)
@CheckAccess("admin")
public class Ajax extends Controller {
	
	public static void webserviceid(final long gameid) {
		Game game = Game.findById(gameid);
		if (game != null) {
			final String webserviceID = params.get("value");
			if (StringUtils.isNotBlank(webserviceID)) {
				game.setWebserviceID(webserviceID);
				game._save();
				ok();
			}
		}
		badRequest();
	}
	
	public static void kickoff(final long gameid) {
		Game game = Game.findById(gameid);
		if (game != null) {
			final String kickoff = params.get("value");
			if (StringUtils.isNotBlank(kickoff)) {
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm", Locale.ENGLISH);
					game.setKickoff(simpleDateFormat.parse(kickoff));
					game.setUpdateble(false);
					game._save();		
				} catch (Exception e) {
					badRequest();
				}
				ok();
			}
		}
		badRequest();
	}
	
	public static void place(final long teamid) {
		Team team = Team.findById(teamid);
		if (team != null) {
			final String place = params.get("value");
			if (StringUtils.isNotBlank(place)) {
				team.setPlace(Integer.valueOf(place));
				team._save();
				
				Bracket bracket = team.getBracket();
				bracket.setUpdateble(false);
 				bracket._save();
				
				ok();
			}
		}
		badRequest();
	}
	
	public static void updateblegame(final long gameid) {
		Game game = Game.findById(gameid);
		if (game != null) {
			
		}
		badRequest();
	}
	
	public static void updateblebracket(final long bracketid) {
		Bracket bracket = Bracket.findById(bracketid);
		if (bracket != null) {
			
		}
		badRequest();
	}
}