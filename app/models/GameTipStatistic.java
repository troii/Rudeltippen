package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_gametipstatistics")
public class GameTipStatistic extends Model{
	@ManyToOne
	private Playday playday;
	
	private int points;
	private int correctTrends;
	private int correctTips;
	private int correctDiffs;
	private int avgPoints;
	
	public Playday getPlayday() {
		return playday;
	}
	
	public void setPlayday(Playday playday) {
		this.playday = playday;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public int getCorrectTrends() {
		return correctTrends;
	}
	
	public void setCorrectTrends(int correctTrends) {
		this.correctTrends = correctTrends;
	}
	
	public int getCorrectTips() {
		return correctTips;
	}
	
	public void setCorrectTips(int correctTips) {
		this.correctTips = correctTips;
	}
	
	public int getCorrectDiffs() {
		return correctDiffs;
	}
	
	public void setCorrectDiffs(int correctDiffs) {
		this.correctDiffs = correctDiffs;
	}
	
	public int getAvgPoints() {
		return avgPoints;
	}
	
	public void setAvgPoints(int avgPoints) {
		this.avgPoints = avgPoints;
	}
}