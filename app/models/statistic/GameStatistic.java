package models.statistic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.Playday;
import play.db.jpa.Model;

@Entity
@Table(name = "rudeltippen_gamestatistics")
public class GameStatistic extends Model {

    @ManyToOne
    private Playday playday;

    @Column(nullable=false)
    private String gameResult;

    private int resultCount;

	public Playday getPlayday() {
		return playday;
	}

	public void setPlayday(Playday playday) {
		this.playday = playday;
	}

	public String getGameResult() {
		return gameResult;
	}

	public void setGameResult(String gameResult) {
		this.gameResult = gameResult;
	}

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}