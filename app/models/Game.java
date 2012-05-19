package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

import play.db.jpa.Model;
import utils.AppUtils;

@Entity
@Table(name="rudeltippen_games")
public class Game extends Model{
	@ManyToOne
	private Playday playday;

	@ManyToOne
	private Stadium stadium;

	@OneToMany(mappedBy = "game", fetch=FetchType.LAZY)
	private List<GameTip> gameTips;

	@ManyToOne
	private Team homeTeam;

	@ManyToOne
	private Team awayTeam;

	@Column(nullable=false)
	private Date kickoff;

	@Column(nullable=false)
	private int number;

	private String homeScore;
	private String awayScore;
	private String homeScoreOT;
	private String awayScoreOT;
	private int homePoints;
	private int awayPoints;
	private boolean overtime;
	private boolean playoff;
	private boolean ended;
	private String overtimeType;
	private String homeReference;
	private String awayReference;
	private String webserviceID;

	public Playday getPlayday() {
		return playday;
	}

	public void setPlayday(Playday playday) {
		this.playday = playday;
	}

	public Stadium getStadium() {
		return stadium;
	}

	public void setStadium(Stadium stadium) {
		this.stadium = stadium;
	}

	public List<GameTip> getGameTips() {
		return gameTips;
	}

	public void setGameTips(List<GameTip> gameTips) {
		this.gameTips = gameTips;
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}

	public Date getKickoff() {
		return kickoff;
	}

	public void setKickoff(Date kickoff) {
		this.kickoff = kickoff;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isOvertime() {
		return overtime;
	}

	public void setOvertime(boolean overtime) {
		this.overtime = overtime;
	}

	public boolean isPlayoff() {
		return playoff;
	}

	public void setPlayoff(boolean playoff) {
		this.playoff = playoff;
	}

	public String getOvertimeType() {
		return overtimeType;
	}

	public void setOvertimeType(String overtimeType) {
		this.overtimeType = overtimeType;
	}

	public String getHomeReference() {
		return homeReference;
	}

	public void setHomeReference(String homeReference) {
		this.homeReference = homeReference;
	}

	public String getAwayReference() {
		return awayReference;
	}

	public void setAwayReference(String awayReference) {
		this.awayReference = awayReference;
	}

	public String getWebserviceID() {
		return webserviceID;
	}

	public void setWebserviceID(String webserviceID) {
		this.webserviceID = webserviceID;
	}

	public boolean isEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}

	public Date getTippEnding() {
		final long time = kickoff.getTime();
		final int offset = AppUtils.getSettings().getMinutesBeforeTip() * 60000 ;

		return new Date (time - offset);
	}

	public int getHomePoints() {
		return homePoints;
	}

	public void setHomePoints(int homePoints) {
		this.homePoints = homePoints;
	}

	public int getAwayPoints() {
		return awayPoints;
	}

	public void setAwayPoints(int awayPoints) {
		this.awayPoints = awayPoints;
	}

	public String getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(String homeScore) {
		this.homeScore = homeScore;
	}

	public String getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(String awayScore) {
		this.awayScore = awayScore;
	}

	public String getHomeScoreOT() {
		return homeScoreOT;
	}

	public void setHomeScoreOT(String homeScoreOT) {
		this.homeScoreOT = homeScoreOT;
	}

	public String getAwayScoreOT() {
		return awayScoreOT;
	}

	public void setAwayScoreOT(String awayScoreOT) {
		this.awayScoreOT = awayScoreOT;
	}

	public Team getWinner() {
		String home, away;
		if (this.overtime) {
			home = this.getHomeScoreOT();
			away = this.getAwayScoreOT();
		} else {
			home = this.getHomeScore();
			away = this.getAwayScore();
		}

		if (StringUtils.isNotBlank(home) && StringUtils.isNotBlank(away)) {
			int homeScore = Integer.parseInt(home);
			int awayScore = Integer.parseInt(away);
			if (homeScore > awayScore) {
				return homeTeam;
			} else {
				return awayTeam;
			}
		}

		return null;
	}

	public Team getLoser() {
		String home, away;
		if (this.overtime) {
			home = this.getHomeScoreOT();
			away = this.getAwayScoreOT();
		} else {
			home = this.getHomeScore();
			away = this.getAwayScore();
		}

		if (StringUtils.isNotBlank(home) && StringUtils.isNotBlank(away)) {
			int homeScore = Integer.parseInt(home);
			int awayScore = Integer.parseInt(away);
			if (homeScore > awayScore) {
				return awayTeam;
			} else {
				return homeTeam;
			}
		}

		return null;
	}

	public boolean isTippable() {
		final Date now = new Date();
		final Settings settings = AppUtils.getSettings();
		final int secondsBefore = settings.getMinutesBeforeTip() * 60000;

		if (this.ended) {
			return false;
		} else if (((kickoff.getTime() - secondsBefore) > now.getTime()) && homeTeam != null && awayTeam != null) {
			return true;
		}

		return false;
	}
}