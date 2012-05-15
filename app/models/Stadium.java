package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_stadiums")
public class Stadium extends Model{
	@Column(nullable=false)
	private String name;

	@OneToMany(mappedBy = "stadium")
	private List<Game> games;

	private String capacity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public String getCapacity() {
		return capacity;
	}

	public void setCapacity(String capacity) {
		this.capacity = capacity;
	}
}
