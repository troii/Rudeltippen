package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_settings")
public class Settings extends Model {
	@Column(nullable=false)
	private String appSalt;

	@Column(nullable=false)
	private String appName;

	@Column(nullable=false)
	private String name;

	@Column(nullable=false)
	private String timeZoneString;

	@Column(nullable=false)
	private String dateString;

	@Column(nullable=false)
	private String timeString;

	@Column(nullable=false)
	private String dateTimeLang;

	@Column(nullable=false)
	private int maxPictureSize;

	@Column(nullable=false)
	private int pointsGameWin;

	@Column(nullable=false)
	private int pointsGameDraw;

	@Column(nullable=false)
	private int pointsTip;

	@Column(nullable=false)
	private int pointsTipDiff;

	@Column(nullable=false)
	private int pointsTipTrend;

	@Column(nullable=false)
	private int minutesBeforeTip;

	@Column(nullable=false)
	private int prePlayoffGames;

	@Column(nullable=false)
	private int playoffTeams;

	private String theme;
	private String lastTweet;
	private String dbName;
	private int dbVersion;
	private boolean informOnNewTipper;
	private boolean playoffs;
	private boolean countFinalResult;
	private boolean enableRegistration;

	public String getAppSalt() {
		return appSalt;
	}

	public void setAppSalt(String appSalt) {
		this.appSalt = appSalt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxPictureSize() {
		return maxPictureSize;
	}

	public void setMaxPictureSize(int maxPictureSize) {
		this.maxPictureSize = maxPictureSize;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTimeZoneString() {
		return timeZoneString;
	}

	public void setTimeZoneString(String timeZoneString) {
		this.timeZoneString = timeZoneString;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public String getDateTimeLang() {
		return dateTimeLang;
	}

	public void setDateTimeLang(String dateTimeLang) {
		this.dateTimeLang = dateTimeLang;
	}

	public int getPointsGameWin() {
		return pointsGameWin;
	}

	public void setPointsGameWin(int pointsGameWin) {
		this.pointsGameWin = pointsGameWin;
	}

	public int getPointsGameDraw() {
		return pointsGameDraw;
	}

	public void setPointsGameDraw(int pointsGameDraw) {
		this.pointsGameDraw = pointsGameDraw;
	}

	public int getPointsTip() {
		return pointsTip;
	}

	public void setPointsTip(int pointsTip) {
		this.pointsTip = pointsTip;
	}

	public int getPointsTipDiff() {
		return pointsTipDiff;
	}

	public void setPointsTipDiff(int pointsTipDiff) {
		this.pointsTipDiff = pointsTipDiff;
	}

	public int getPointsTipTrend() {
		return pointsTipTrend;
	}

	public void setPointsTipTrend(int pointsTipTrend) {
		this.pointsTipTrend = pointsTipTrend;
	}

	public int getMinutesBeforeTip() {
		return minutesBeforeTip;
	}

	public void setMinutesBeforeTip(int minutesBeforeTip) {
		this.minutesBeforeTip = minutesBeforeTip;
	}

	public int getPrePlayoffGames() {
		return prePlayoffGames;
	}

	public void setPrePlayoffGames(int prePlayoffGames) {
		this.prePlayoffGames = prePlayoffGames;
	}

	public int getPlayoffTeams() {
		return playoffTeams;
	}

	public void setPlayoffTeams(int playoffTeams) {
		this.playoffTeams = playoffTeams;
	}

	public boolean isInformOnNewTipper() {
		return informOnNewTipper;
	}

	public void setInformOnNewTipper(boolean informOnNewTipper) {
		this.informOnNewTipper = informOnNewTipper;
	}

	public boolean isPlayoffs() {
		return playoffs;
	}

	public void setPlayoffs(boolean playoffs) {
		this.playoffs = playoffs;
	}

	public boolean isCountFinalResult() {
		return countFinalResult;
	}

	public void setCountFinalResult(boolean countFinalResult) {
		this.countFinalResult = countFinalResult;
	}

	public boolean isEnableRegistration() {
		return enableRegistration;
	}

	public void setEnableRegistration(boolean enableRegistration) {
		this.enableRegistration = enableRegistration;
	}

	public String getLastTweet() {
		return lastTweet;
	}

	public void setLastTweet(String lastTweet) {
		this.lastTweet = lastTweet;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public int getDbVersion() {
		return dbVersion;
	}

	public void setDbVersion(int dbVersion) {
		this.dbVersion = dbVersion;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
}