package models.statistic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.Playday;
import play.db.jpa.Model;

@Entity
@Table(name = "rudeltippen_playdaystatistics")
public class PlaydayStatistic extends Model {
    @ManyToOne
    private Playday playday;

    @Column(nullable=false)
    private String gameResult;

    private int resultCount;

    public Playday getPlayday() {
        return this.playday;
    }

    public void setPlayday(final Playday playday) {
        this.playday = playday;
    }

    public String getGameResult() {
        return this.gameResult;
    }

    public void setGameResult(final String gameResult) {
        this.gameResult = gameResult;
    }

	public int getResultCount() {
		return resultCount;
	}

	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
}