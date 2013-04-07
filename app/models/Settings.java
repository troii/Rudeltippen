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
	private String gameName;

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
	private int numPrePlayoffGames;

	@Column(nullable=false)
	private int numPlayoffTeams;

	private String lastTweet;
	private boolean informOnNewTipper;
	private boolean playoffs;
	private boolean countFinalResult;
	private boolean enableRegistration;

	public String getAppSalt() {
		return appSalt;
	}

	public void setAppSalt(final String appSalt) {
		this.appSalt = appSalt;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(final String gameName) {
		this.gameName = gameName;
	}

	public int getMaxPictureSize() {
		return maxPictureSize;
	}

	public void setMaxPictureSize(final int maxPictureSize) {
		this.maxPictureSize = maxPictureSize;
	}

	public int getPointsGameWin() {
		return pointsGameWin;
	}

	public void setPointsGameWin(final int pointsGameWin) {
		this.pointsGameWin = pointsGameWin;
	}

	public int getPointsGameDraw() {
		return pointsGameDraw;
	}

	public void setPointsGameDraw(final int pointsGameDraw) {
		this.pointsGameDraw = pointsGameDraw;
	}

	public int getPointsTip() {
		return pointsTip;
	}

	public void setPointsTip(final int pointsTip) {
		this.pointsTip = pointsTip;
	}

	public int getPointsTipDiff() {
		return pointsTipDiff;
	}

	public void setPointsTipDiff(final int pointsTipDiff) {
		this.pointsTipDiff = pointsTipDiff;
	}

	public int getPointsTipTrend() {
		return pointsTipTrend;
	}

	public void setPointsTipTrend(final int pointsTipTrend) {
		this.pointsTipTrend = pointsTipTrend;
	}

	public int getMinutesBeforeTip() {
		return minutesBeforeTip;
	}

	public void setMinutesBeforeTip(final int minutesBeforeTip) {
		this.minutesBeforeTip = minutesBeforeTip;
	}

	public int getNumPrePlayoffGames() {
		return numPrePlayoffGames;
	}

	public void setNumPrePlayoffGames(final int numPrePlayoffGames) {
		this.numPrePlayoffGames = numPrePlayoffGames;
	}

	public int getNumPlayoffTeams() {
		return numPlayoffTeams;
	}

	public void setNumPlayoffTeams(final int numPlayoffTeams) {
		this.numPlayoffTeams = numPlayoffTeams;
	}

	public String getLastTweet() {
		return lastTweet;
	}

	public void setLastTweet(final String lastTweet) {
		this.lastTweet = lastTweet;
	}

	public boolean isInformOnNewTipper() {
		return informOnNewTipper;
	}

	public void setInformOnNewTipper(final boolean informOnNewTipper) {
		this.informOnNewTipper = informOnNewTipper;
	}

	public boolean isPlayoffs() {
		return playoffs;
	}

	public void setPlayoffs(final boolean playoffs) {
		this.playoffs = playoffs;
	}

	public boolean isCountFinalResult() {
		return countFinalResult;
	}

	public void setCountFinalResult(final boolean countFinalResult) {
		this.countFinalResult = countFinalResult;
	}

	public boolean isEnableRegistration() {
		return enableRegistration;
	}

	public void setEnableRegistration(final boolean enableRegistration) {
		this.enableRegistration = enableRegistration;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(final String appName) {
		this.appName = appName;
	}
}