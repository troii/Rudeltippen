package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_stadiums")
public class Stadium extends Model{
	@Column(nullable=false)
	private String name;

	@OneToMany(mappedBy = "stadium", fetch=FetchType.LAZY)
	private List<Game> games;

	@Column(nullable=false)
	private String city;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(final List<Game> games) {
		this.games = games;
	}

	public String getCity() {
		return city;
	}

	public void setCity(final String city) {
		this.city = city;
	}
}
