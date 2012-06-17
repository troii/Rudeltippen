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

	@Column(nullable=false)
	private int number;

	private boolean overridePlaces;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isOverridePlaces() {
		return overridePlaces;
	}

	public void setOverridePlaces(boolean overridePlaces) {
		this.overridePlaces = overridePlaces;
	}

	public Team getTeamByPlace(int place) {
		int i = 1;
		for (Team team : teams) {
			if  (i == place) {
				return team;
			}
			i++;
		}

		return null;
	}
}