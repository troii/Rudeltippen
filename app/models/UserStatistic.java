package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "rudeltippen_userstatistics")
public class UserStatistic extends Model {
    @ManyToOne
    private Playday playday;

    @ManyToOne
    private User user;

    private int place;
    private int points;
    private int correctTips;
    private int correctTrends;
    private int correctDiffs;

    public Playday getPlayday() {
        return this.playday;
    }

    public void setPlayday(final Playday playday) {
        this.playday = playday;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public int getPlace() {
        return this.place;
    }

    public void setPlace(final int place) {
        this.place = place;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public int getCorrectTips() {
        return this.correctTips;
    }

    public void setCorrectTips(final int correctTips) {
        this.correctTips = correctTips;
    }

    public int getCorrectTrends() {
        return this.correctTrends;
    }

    public void setCorrectTrends(final int correctTrends) {
        this.correctTrends = correctTrends;
    }

    public int getCorrectDiffs() {
        return this.correctDiffs;
    }

    public void setCorrectDiffs(final int correctDiffs) {
        this.correctDiffs = correctDiffs;
    }
}