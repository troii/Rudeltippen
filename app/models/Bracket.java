package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_brackets")
public class Bracket extends Model{
	@Column(nullable=false)
	private String name;

	@OneToMany(mappedBy = "bracket")
	@OrderBy("place ASC")
	private List<Team> teams;

	@OneToMany(mappedBy = "bracket")
	@OrderBy("number ASC")
	private List<Game> games;

	@Column(nullable=false)
	private int number;

	private boolean updateble;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(final List<Team> teams) {
		this.teams = teams;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(final List<Game> games) {
		this.games = games;
	}

	public boolean isUpdateble() {
		return updateble;
	}

	public void setUpdateble(boolean updateble) {
		this.updateble = updateble;
	}

	public boolean allGamesEnded() {
		for (final Game game : games) {
			if (!game.isEnded()) {
				return false;
			}
		}

		return true;
	}

	public Team getTeamByPlace(final int place) {
		int i = 1;
		for (final Team team : teams) {
			if  (i == place) {
				return team;
			}
			i++;
		}

		return null;
	}
}