package models.statistic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.User;
import play.db.jpa.Model;

@Entity
@Table(name = "rudeltippen_resultstatistics")
public class ResultStatistic extends Model{
    @ManyToOne
	private User user;
    
    @Column(nullable=false)
	private String result;
    
	private int correctTips;
	private int correctTrends;
	private int correctDiffs;
	private int points;
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public int getCorrectTips() {
		return correctTips;
	}
	
	public void setCorrectTips(int correctTips) {
		this.correctTips = correctTips;
	}
	
	public int getCorrectTrends() {
		return correctTrends;
	}
	
	public void setCorrectTrends(int correctTrends) {
		this.correctTrends = correctTrends;
	}
	
	public int getCorrectDiffs() {
		return correctDiffs;
	}
	
	public void setCorrectDiffs(int correctDiffs) {
		this.correctDiffs = correctDiffs;
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
}