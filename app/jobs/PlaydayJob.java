package jobs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import models.AbstractJob;
import models.Game;
import models.Playday;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import play.Logger;
import play.i18n.Messages;
import play.jobs.On;
import utils.AppUtils;
import utils.SetupUtils;
import utils.WSUtils;

@On("0 0 5 * * ?")
public class PlaydayJob extends AppJob{

    public PlaydayJob() {
        this.setDescription(Messages.get("job.playdayjob.description"));
        this.setExecuted(Messages.get("job.playdayjob.executed"));
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            AbstractJob job = AbstractJob.find("byName", "PlaydayJob").first();
            if (job != null && job.isActive()) {
                Logger.info("Started Job: PlaydayJob");
                int number = AppUtils.getCurrentPlayday().getNumber();
                for (int i=0; i <= 3; i++) {
                    final Playday playday = Playday.find("byNumber", number).first();
                    if (playday != null) {
                        final List<Game> games = playday.getGames();
                        for (final Game game : games) {
                            final String matchID = game.getWebserviceID();
                            if (StringUtils.isNotBlank(matchID) && game.isUpdateble()) {
                                final Document document = WSUtils.getDocumentFromWebService(matchID);
                                final Date kickoff = SetupUtils.getKickoffFromDocument(document);
                                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                                df.setTimeZone(TimeZone.getTimeZone(AppUtils.getCurrentTimeZone()));

                                game.setKickoff(kickoff);
                                game._save();

                                Logger.info("Updated Kickoff and MatchID of Playday: " + playday.getName());
                            }
                        }
                    }
                    number++;
                }
                Logger.info("Finished Job: PlaydayJob");
            }
        }
    }
}