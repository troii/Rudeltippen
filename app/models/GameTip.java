package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_gametipps")
public class GameTip extends Model{
	@ManyToOne
	private User user;

	@ManyToOne
	private Game game;

	@Column(nullable=false)
	private int homeScore;

	@Column(nullable=false)
	private int awayScore;

	@Column(nullable=false)
	private Date placed;

	private int points;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}

	public Date getPlaced() {
		return placed;
	}

	public void setPlaced(Date placed) {
		this.placed = placed;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}