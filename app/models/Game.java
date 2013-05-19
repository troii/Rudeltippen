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
    private Bracket bracket;

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

    private String overtimeType;
    private String homeReference;
    private String awayReference;
    private String webserviceID;
    private String homeScore;
    private String awayScore;
    private String homeScoreOT;
    private String awayScoreOT;
    private int homePoints;
    private int awayPoints;
    private boolean overtime;
    private boolean playoff;
    private boolean ended;
    private boolean informed;

    public Playday getPlayday() {
        return this.playday;
    }

    public void setPlayday(final Playday playday) {
        this.playday = playday;
    }

    public List<GameTip> getGameTips() {
        return this.gameTips;
    }

    public void setGameTips(final List<GameTip> gameTips) {
        this.gameTips = gameTips;
    }

    public Team getHomeTeam() {
        return this.homeTeam;
    }

    public void setHomeTeam(final Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        return this.awayTeam;
    }

    public void setAwayTeam(final Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Date getKickoff() {
        return this.kickoff;
    }

    public void setKickoff(final Date kickoff) {
        this.kickoff = kickoff;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public boolean isOvertime() {
        return this.overtime;
    }

    public void setOvertime(final boolean overtime) {
        this.overtime = overtime;
    }

    public boolean isPlayoff() {
        return this.playoff;
    }

    public void setPlayoff(final boolean playoff) {
        this.playoff = playoff;
    }

    public String getOvertimeType() {
        return this.overtimeType;
    }

    public void setOvertimeType(final String overtimeType) {
        this.overtimeType = overtimeType;
    }

    public String getHomeReference() {
        return this.homeReference;
    }

    public void setHomeReference(final String homeReference) {
        this.homeReference = homeReference;
    }

    public String getAwayReference() {
        return this.awayReference;
    }

    public void setAwayReference(final String awayReference) {
        this.awayReference = awayReference;
    }

    public String getWebserviceID() {
        return this.webserviceID;
    }

    public void setWebserviceID(final String webserviceID) {
        this.webserviceID = webserviceID;
    }

    public boolean isEnded() {
        return this.ended;
    }

    public void setEnded(final boolean ended) {
        this.ended = ended;
    }

    public Date getTippEnding() {
        final long time = this.kickoff.getTime();
        final int offset = AppUtils.getSettings().getMinutesBeforeTip() * 60000 ;

        return new Date (time - offset);
    }

    public int getHomePoints() {
        return this.homePoints;
    }

    public void setHomePoints(final int homePoints) {
        this.homePoints = homePoints;
    }

    public int getAwayPoints() {
        return this.awayPoints;
    }

    public void setAwayPoints(final int awayPoints) {
        this.awayPoints = awayPoints;
    }

    public String getHomeScore() {
        return this.homeScore;
    }

    public void setHomeScore(final String homeScore) {
        this.homeScore = homeScore;
    }

    public String getAwayScore() {
        return this.awayScore;
    }

    public void setAwayScore(final String awayScore) {
        this.awayScore = awayScore;
    }

    public String getHomeScoreOT() {
        return this.homeScoreOT;
    }

    public void setHomeScoreOT(final String homeScoreOT) {
        this.homeScoreOT = homeScoreOT;
    }

    public String getAwayScoreOT() {
        return this.awayScoreOT;
    }

    public void setAwayScoreOT(final String awayScoreOT) {
        this.awayScoreOT = awayScoreOT;
    }

    public Bracket getBracket() {
        return this.bracket;
    }

    public void setBracket(final Bracket bracket) {
        this.bracket = bracket;
    }

    public boolean isInformed() {
        return this.informed;
    }

    public void setInformed(final boolean informed) {
        this.informed = informed;
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
            final int homeScore = Integer.parseInt(home);
            final int awayScore = Integer.parseInt(away);
            if (homeScore > awayScore) {
                return this.homeTeam;
            } else {
                return this.awayTeam;
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
            final int homeScore = Integer.parseInt(home);
            final int awayScore = Integer.parseInt(away);
            if (homeScore > awayScore) {
                return this.awayTeam;
            } else {
                return this.homeTeam;
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
        } else if (((this.kickoff.getTime() - secondsBefore) > now.getTime()) && (this.homeTeam != null) && (this.awayTeam != null)) {
            return true;
        }

        return false;
    }
}