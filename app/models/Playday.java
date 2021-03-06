package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import models.statistic.GameStatistic;
import models.statistic.GameTipStatistic;
import models.statistic.UserStatistic;
import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_playdays")
public class Playday extends Model{
    @OneToMany(mappedBy = "playday")
    @OrderBy("kickoff ASC")
    private List<Game> games;

    @OneToMany(mappedBy = "playday", fetch = FetchType.LAZY)
    private List<UserStatistic> userStatistics;
    
    @OneToMany(mappedBy = "playday", fetch = FetchType.LAZY)
    private List<GameTipStatistic> gameTipStatistics;
    
    @OneToMany(mappedBy = "playday", fetch = FetchType.LAZY)
    private List<GameStatistic> gameStatistic;
    
    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private int number;

    private boolean playoff;
    private boolean current;

    public boolean isCurrent() {
        return this.current;
    }

    public void setCurrent(final boolean current) {
        this.current = current;
    }

    public List<Game> getGames() {
        return this.games;
    }

    public void setGames(final List<Game> games) {
        this.games = games;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public boolean isPlayoff() {
        return this.playoff;
    }

    public void setPlayoff(final boolean playoff) {
        this.playoff = playoff;
    }

    public boolean isTippable() {
        for (final Game game : this.games){
            if (game.isTippable()) {
                return true;
            }
        }
        return false;
    }

    public boolean allGamesEnded() {
        for (final Game game : this.games) {
            if (!game.isEnded()) {
                return false;
            }
        }
        return true;
    }

	public List<UserStatistic> getUserStatistics() {
		return userStatistics;
	}

	public void setUserStatistics(List<UserStatistic> userStatistics) {
		this.userStatistics = userStatistics;
	}

	public List<GameTipStatistic> getGameTipStatistics() {
		return gameTipStatistics;
	}

	public void setGameTipStatistics(List<GameTipStatistic> gameTipStatistics) {
		this.gameTipStatistics = gameTipStatistics;
	}

	public List<GameStatistic> getGameStatistic() {
		return gameStatistic;
	}

	public void setGameStatistic(List<GameStatistic> gameStatistic) {
		this.gameStatistic = gameStatistic;
	}
}