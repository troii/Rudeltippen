package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import models.statistic.ResultStatistic;
import models.statistic.UserStatistic;

import play.db.jpa.Model;

@Entity
@Table(name = "rudeltippen_users")
public class User extends Model {
    @Column(nullable = false)
    private String userpass;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String salt;

    @Column(nullable = false)
    private Date registered;

    @Lob
    private String picture;

    @Lob
    private String pictureLarge;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<GameTip> gameTips;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ExtraTip> extraTips;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Confirmation> confirmations;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserStatistic> userStatistics;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ResultStatistic> resultStatistic;

    private Date lastLogin;
    private boolean reminder;
    private boolean admin;
    private boolean active;
    private boolean notification;
    private boolean sendStandings;
    private int tipPoints;
    private int extraPoints;
    private int points;
    private int place;
    private int previousPlace;
    private int correctResults;
    private int correctDifferences;
    private int correctTrends;
    private int correctExtraTips;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getUserpass() {
        return this.userpass;
    }

    public void setUserpass(final String userpass) {
        this.userpass = userpass;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(final String salt) {
        this.salt = salt;
    }

    public Date getRegistered() {
        return this.registered;
    }

    public void setRegistered(final Date registered) {
        this.registered = registered;
    }

    public String getPicture() {
        return this.picture;
    }

    public void setPicture(final String picture) {
        this.picture = picture;
    }

    public String getPictureLarge() {
        return this.pictureLarge;
    }

    public void setPictureLarge(final String pictureLarge) {
        this.pictureLarge = pictureLarge;
    }

    public List<GameTip> getGameTips() {
        return this.gameTips;
    }

    public void setGameTips(final List<GameTip> gameTips) {
        this.gameTips = gameTips;
    }

    public List<ExtraTip> getExtraTips() {
        return this.extraTips;
    }

    public void setExtraTips(final List<ExtraTip> extraTips) {
        this.extraTips = extraTips;
    }

    public List<Confirmation> getConfirmations() {
        return this.confirmations;
    }

    public void setConfirmations(final List<Confirmation> confirmations) {
        this.confirmations = confirmations;
    }

    public boolean isReminder() {
        return this.reminder;
    }

    public void setReminder(final boolean reminder) {
        this.reminder = reminder;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Date getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(final Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public int getPoints() {
        return this.points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }

    public int getTipPoints() {
        return this.tipPoints;
    }

    public void setTipPoints(final int tipPoints) {
        this.tipPoints = tipPoints;
    }

    public int getExtraPoints() {
        return this.extraPoints;
    }

    public void setExtraPoints(final int extraPoints) {
        this.extraPoints = extraPoints;
    }

    public int getPlace() {
        return this.place;
    }

    public void setPlace(final int place) {
        this.place = place;
    }

    public boolean isNotification() {
        return this.notification;
    }

    public void setNotification(final boolean notification) {
        this.notification = notification;
    }

    public int getCorrectResults() {
        return this.correctResults;
    }

    public void setCorrectResults(final int correctResults) {
        this.correctResults = correctResults;
    }

    public int getCorrectDifferences() {
        return this.correctDifferences;
    }

    public void setCorrectDifferences(final int correctDifferences) {
        this.correctDifferences = correctDifferences;
    }

    public int getCorrectTrends() {
        return this.correctTrends;
    }

    public void setCorrectTrends(final int correctTrends) {
        this.correctTrends = correctTrends;
    }

    public int getCorrectExtraTips() {
        return this.correctExtraTips;
    }

    public void setCorrectExtraTips(final int correctExtraTips) {
        this.correctExtraTips = correctExtraTips;
    }

    public int getPreviousPlace() {
        return this.previousPlace;
    }

    public void setPreviousPlace(final int previousPlace) {
        this.previousPlace = previousPlace;
    }

    public boolean isSendStandings() {
        return this.sendStandings;
    }

    public void setSendStandings(final boolean sendStandings) {
        this.sendStandings = sendStandings;
    }

	public List<UserStatistic> getUserStatistics() {
		return userStatistics;
	}

	public void setUserStatistics(List<UserStatistic> userStatistics) {
		this.userStatistics = userStatistics;
	}

	public List<ResultStatistic> getResultStatistic() {
		return resultStatistic;
	}

	public void setResultStatistic(List<ResultStatistic> resultStatistic) {
		this.resultStatistic = resultStatistic;
	}
}