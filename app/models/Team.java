package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;
import play.i18n.Messages;

@Entity
@Table(name="rudeltippen_teams")
public class Team extends Model{
	@Column(nullable=false)
	private String name;

	@ManyToOne
	private Bracket bracket;

	@OneToMany(mappedBy = "homeTeam", fetch=FetchType.LAZY)
	@OrderBy("kickoff ASC")
	private List<Game> homeGames;

	@OneToMany(mappedBy = "awayTeam", fetch=FetchType.LAZY)
	@OrderBy("kickoff ASC")
	private List<Game> awayGames;

	@Column(nullable=false)
	private String flag;

	private String showrtName;
	private int points;
	private int goalsFor;
	private int goalsAgainst;
	private int goalsDiff;
	private int gamesPlayed;
	private int gamesWon;
	private int gamesDraw;
	private int gamesLost;
	private int place;
	private int previousPlace;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Bracket getBracket() {
		return this.bracket;
	}

	public void setBracket(final Bracket bracket) {
		this.bracket = bracket;
	}

	public List<Game> getHomeGames() {
		return this.homeGames;
	}

	public void setHomeGames(final List<Game> homeGames) {
		this.homeGames = homeGames;
	}

	public List<Game> getAwayGames() {
		return this.awayGames;
	}

	public void setAwayGames(final List<Game> awayGames) {
		this.awayGames = awayGames;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(final String flag) {
		this.flag = flag;
	}

	public int getPoints() {
		return this.points;
	}

	public void setPoints(final int points) {
		this.points = points;
	}

	public int getGoalsFor() {
		return this.goalsFor;
	}

	public void setGoalsFor(final int goalsFor) {
		this.goalsFor = goalsFor;
	}

	public int getGoalsAgainst() {
		return this.goalsAgainst;
	}

	public void setGoalsAgainst(final int goalsAgainst) {
		this.goalsAgainst = goalsAgainst;
	}

	public int getGoalsDiff() {
		return this.goalsDiff;
	}

	public void setGoalsDiff(final int goalsDiff) {
		this.goalsDiff = goalsDiff;
	}

	public int getGamesPlayed() {
		return this.gamesPlayed;
	}

	public void setGamesPlayed(final int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}

	public int getGamesWon() {
		return this.gamesWon;
	}

	public void setGamesWon(final int gamesWon) {
		this.gamesWon = gamesWon;
	}

	public int getGamesDraw() {
		return this.gamesDraw;
	}

	public void setGamesDraw(final int gamesDraw) {
		this.gamesDraw = gamesDraw;
	}

	public int getGamesLost() {
		return this.gamesLost;
	}

	public void setGamesLost(final int gamesLost) {
		this.gamesLost = gamesLost;
	}

	public int getPlace() {
		return this.place;
	}

	public void setPlace(final int place) {
		this.place = place;
	}

	public int getPreviousPlace() {
		return this.previousPlace;
	}

	public void setPreviousPlace(final int previousPlace) {
		this.previousPlace = previousPlace;
	}

	public String nameUnescaped() {
		if (StringUtils.isNotBlank(this.name)) {
			final String name = Messages.get(this.name);
			return StringEscapeUtils.unescapeHtml(name);
		}

		return "";
	}

	public String getShowrtName() {
		return this.showrtName;
	}

	public void setShowrtName(final String showrtName) {
		this.showrtName = showrtName;
	}
}