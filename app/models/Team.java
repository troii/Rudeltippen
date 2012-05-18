package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_teams")
public class Team extends Model{
	@Column(nullable=false)
	private String name;

	@ManyToOne
	private Bracket bracket;

	@OneToMany(mappedBy = "homeTeam", fetch=FetchType.LAZY)
	@OrderBy("kickoff")
	private List<Game> homeGames;

	@OneToMany(mappedBy = "awayTeam", fetch=FetchType.LAZY)
	@OrderBy("kickoff")
	private List<Game> awayGames;

	@Column(nullable=false)
	private String flag;

	private int points;
	private int goalsFor;
	private int goalsAgainst;
	private int goalsDiff;
	private int gamesPlayed;
	private int gamesWon;
	private int gamesDraw;
	private int gamesLost;
	private int place;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bracket getBracket() {
		return bracket;
	}

	public void setBracket(Bracket bracket) {
		this.bracket = bracket;
	}

	public List<Game> getHomeGames() {
		return homeGames;
	}

	public void setHomeGames(List<Game> homeGames) {
		this.homeGames = homeGames;
	}

	public List<Game> getAwayGames() {
		return awayGames;
	}

	public void setAwayGames(List<Game> awayGames) {
		this.awayGames = awayGames;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getGoalsFor() {
		return goalsFor;
	}

	public void setGoalsFor(int goalsFor) {
		this.goalsFor = goalsFor;
	}

	public int getGoalsAgainst() {
		return goalsAgainst;
	}

	public void setGoalsAgainst(int goalsAgainst) {
		this.goalsAgainst = goalsAgainst;
	}

	public int getGoalsDiff() {
		return goalsDiff;
	}

	public void setGoalsDiff(int goalsDiff) {
		this.goalsDiff = goalsDiff;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public int getGamesWon() {
		return gamesWon;
	}

	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}

	public int getGamesDraw() {
		return gamesDraw;
	}

	public void setGamesDraw(int gamesDraw) {
		this.gamesDraw = gamesDraw;
	}

	public int getGamesLost() {
		return gamesLost;
	}

	public void setGamesLost(int gamesLost) {
		this.gamesLost = gamesLost;
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}
}