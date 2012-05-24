package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_playdays")
public class Playday extends Model{
	@OneToMany(mappedBy = "playday")
	private List<Game> games;

	@Column(nullable=false)
	private String name;

	@Column(nullable=false)
	private Date playdayStart;

	@Column(nullable=false)
	private Date playdayEnd;

	@Column(nullable=false)
	private int number;

	private boolean playoff;

	public List<Game> getGames() {
		return games;
	}

	public void setGames(List<Game> games) {
		this.games = games;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getPlaydayStart() {
		return playdayStart;
	}

	public void setPlaydayStart(Date playdayStart) {
		this.playdayStart = playdayStart;
	}

	public Date getPlaydayEnd() {
		return playdayEnd;
	}

	public void setPlaydayEnd(Date playdayEnd) {
		this.playdayEnd = playdayEnd;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isPlayoff() {
		return playoff;
	}

	public void setPlayoff(boolean playoff) {
		this.playoff = playoff;
	}
	
	public boolean isTippable() {
		for (Game game : this.games){
			if (game.isTippable()) {
				return true;
			}
		}
		
		return false;
	}
}