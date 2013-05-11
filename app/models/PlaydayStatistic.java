package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name = "rudeltippen_playdaystatistics")
public class PlaydayStatistic extends Model {
    @OneToOne
    private Playday playday;

    @Column(nullable=false)
    private String gameResult;

    private int resoultCount;

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

    public int getResoultCount() {
        return this.resoultCount;
    }

    public void setResoultCount(final int resoultCount) {
        this.resoultCount = resoultCount;
    }
}