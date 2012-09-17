package controllers;

import java.util.List;

import models.Bracket;
import models.Extra;
import models.Game;
import models.Playday;
import models.Team;
import models.User;
import play.db.jpa.Transactional;
import play.mvc.Before;
import play.mvc.Controller;
import utils.AppUtils;
import utils.ValidationUtils;
import flexjson.JSONSerializer;

public class API extends Controller {
	@Before
	protected static void auth() {
		if (AppUtils.isAPI()) {
			final User connectedUser = AppUtils.getConnectedUser();
			if (connectedUser == null) {
				final String username = request.user;
				final String userpass = request.password;

				final boolean allowed = authenticate(username, userpass);
				if (!allowed) {
					unauthorized("Rudeltippen API");
				}				
			}
		} else {
			forbidden("Rudeltippen API is not enabled");
		}
	}
	
	@Transactional(readOnly=true)
	public static void standings() {
		List<User> users = User.find("ORDER BY place ASC").fetch();
		if (users != null) {
			JSONSerializer usersSerializer = new JSONSerializer()
				.include("username", "nickname", "registered", "picture")
				.include("tipPoints", "extraPoints", "points", "place", "pictureLarge", "previousPlace")
				.include("correctResults", "currectDifferences", "correctTrends", "correctExtraTips")
				.exclude("*");
			
			renderJSON(usersSerializer.serialize(users));
		}
		
		error(501, "Could not find any users");
	}
	
	@Transactional(readOnly=true)
	public static void tournament() {
		List<Bracket> brackets = Bracket.findAll();
		if (brackets != null) {
			JSONSerializer bracketsSerializer = new JSONSerializer()
				.include("name", "number")
				.include("teams.name", "teams.flag", "teams.points", "teams.goalsFor", "teams.goalsAgainst", "teams.goalsDiff")
				.include("teams.gamesPlayed", "teams.gamesWon", "teams.gamesDraw", "team.gamesLost")
				.include("teams.place")
				.exclude("*");
		
			renderJSON(bracketsSerializer.serialize(brackets));
		}

		error(501, "Could not find any bracksts");
	}
	
	@Transactional(readOnly=true)
	public static void user(String nickname) {
		final User user = User.find("byNickname", nickname).first();
		if (user != null) {
			JSONSerializer userSerializer = new JSONSerializer()
				.include("username", "nickname", "registered", "picture")
				.include("tipPoints", "extraPoints", "points", "place", "pictureLarge", "previousPlace")
				.include("correctResults", "currectDifferences", "correctTrends", "correctExtraTips")
				.exclude("*");
		
			renderJSON(userSerializer.serialize(user));
		}
		
		error(501, "Could not finde user: " + nickname);
	}
	
	@Transactional(readOnly=true)
	public static void playday(int number) {
		final Playday playday = Playday.find("byNumber", number).first();
		if (playday != null) {
			JSONSerializer playdaySerializer = new JSONSerializer()
				.include("name", "number", "playoff", "playdayStart", "playdayEnd")
				.include("games.homeTeam.name", "games.awayTeam.name")
				.include("games.homeScore", "games.awayScore")
				.include("games.homeScoreOT", "games.awayScoreOT")
				.include("games.ended", "games.stadium.name", "games.kickoff")
				.include("games.overtime", "games.overtimeType")
				.exclude("*");
		
			renderJSON(playdaySerializer.serialize(playday));			
		}
		
		error(501, "Could not find playday with number: " + number);
	}
	
	public static void storetip(long gameid, String homeScore, String awayScore) {
		Game game = Game.findById(gameid);
		
		if (game == null) {
			error(501, "Could not find game with game id: " + gameid);
		}
		
		if (!game.isTippable()) {
			error(501, "Game is not tipable any more");
		}

		if (ValidationUtils.isValidScore(homeScore, awayScore)) {
			AppUtils.placeTip(game, Integer.parseInt(homeScore), Integer.parseInt(awayScore));
			ok();
		} else {
			error(501, "Invalid score");
		}
		
		badRequest();
	}
	
	public static void storeextratip(long extraid, long teamid) {
		Extra extra = Extra.findById(extraid);
		Team team = Team.findById(teamid);
		
		if (extra == null) {
			error(501, "Could not find extra with game id: " + extraid);
		}
		
		if (team == null) {
			error(501, "Could not find team with id: " + teamid);
		}
		
		if (!extra.isTipable()) {
			error(501, "Extra is not tipable any more");
		} else {
			AppUtils.placeExtraTip(extra, team);
			ok();
		}
		
		badRequest();
	}
	
    private static boolean authenticate(String username, String userpass) {
    	String usersalt = null;
    	
    	User user = User.find("byUsername", username).first();
    	if (user != null) {
    		usersalt = user.getSalt();
    		if (User.connect(username, AppUtils.hashPassword(userpass, usersalt)) != null) {
    			session.put("username", username);
    			return true;
    		}
    	}

    	return false;
    }
}