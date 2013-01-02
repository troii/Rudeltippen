package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_statistics")
public class Statistic extends Model{
	@ManyToOne
	private User user;
	
	private int playday;
	private int points;

	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}

	public int getPlayday() {
		return playday;
	}

	public void setPlayday(int playday) {
		this.playday = playday;
	}
}