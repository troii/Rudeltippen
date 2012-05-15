package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_extratipps")
public class ExtraTip extends Model{
	@ManyToOne
	private User user;

	@ManyToOne
	private Extra extra;

	@ManyToOne
	public Team answer;

	private int points;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Extra getExtra() {
		return extra;
	}

	public void setExtra(Extra extra) {
		this.extra = extra;
	}

	public Team getAnswer() {
		return answer;
	}

	public void setAnswer(Team answer) {
		this.answer = answer;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
}
