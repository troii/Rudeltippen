package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_settings")
public class Settings extends Model {
	private String appSalt;//No
	private String appName;//No
	private String gameName;//Yes
	private String lastTweet;//No
	
	private int pointsGameWin;//No
	private int pointsGameDraw;//No
	private int pointsTip;//Yes
	private int pointsTipDiff;//Yes
	private int pointsTipTrend;//Yes
	private int minutesBeforeTip;//Yes
	private int numPrePlayoffGames;//No
	private int numPlayoffTeams;//No

	private boolean informOnNewTipper;//Yes
	private boolean playoffs;//No
	private boolean countFinalResult;//Yes
	private boolean enableRegistration;//Yes
	
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