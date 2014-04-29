package jobs;

import java.util.List;

import models.AbstractJob;
import models.Game;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Every;
import utils.AppUtils;

@Every("1min")
public class GameTipJob extends AppJob{

    public GameTipJob() {
        this.setDescription(Messages.get("job.gametipjob.description"));
        this.setExecuted(Messages.get("job.gametipjob.executed"));
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            AbstractJob job = AbstractJob.find("byName", "GameTipJob").first();
            if (job != null && job.isActive()) {
                Logger.info("Started Job: GameTipJob");
                final List<User> users = User.find("SELECT u FROM User u WHERE active = ? AND sendGameTips = ?", true, true).fetch();
                final List<Game> games = Game.find("SELECT g FROM Game g WHERE informed = ? AND ( TIMESTAMPDIFF(MINUTE,kickoff,now()) > 1 )", false).fetch();

                if (games != null && games.size() > 0) {
                    for (final User user : users) {
                        Mails.gametips(user, games);
                    }

                    for (final Game game : games) {
                        game.setInformed(true);
                        game._save();
                    }
                }

                Logger.info("Finished Job: GameTipJob");
            }
        }
    }
}