package jobs;

import java.util.ArrayList;
import java.util.List;

import models.AbstractJob;
import models.Extra;
import models.ExtraTip;
import models.Game;
import models.GameTip;
import models.User;
import notifiers.Mails;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Every;
import utils.AppUtils;

@Every("1h")
public class ReminderJob extends AppJob {

    public ReminderJob() {
        this.setDescription(Messages.get("job.reminderjob.description"));
        this.setExecuted(Messages.get("job.reminderjob.executed"));
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            AbstractJob job = AbstractJob.find("byName", "PlaydayJob").first();
            if (job != null && job.isActive()) {
                Logger.info("Started Job: ReminderJob");
                final List<Extra> nextExtras = Extra.find("SELECT e FROM Extra e WHERE reminder = ? AND ( TIMESTAMPDIFF(HOUR,ending,now()) > 0 ) AND ( TIMESTAMPDIFF(HOUR,ending,now()) < 24 )", false).fetch();
                final List<Game> nextGames = Game.find("SELECT g FROM Game g WHERE  reminder = ? AND ( TIMESTAMPDIFF(HOUR,kickoff,now()) > 0 ) AND ( TIMESTAMPDIFF(HOUR,kickoff,now()) < 24 )", false).fetch();
                final List<User> users = User.find("byReminderAndActive", true, true).fetch();

                for (final User user : users) {
                    final List<Game> reminderGames = new ArrayList();
                    final List<Extra> reminderBonus = new ArrayList();

                    for (final Game game : nextGames) {
                        final GameTip gameTip = GameTip.find("byGameAndUser", game, user).first();
                        if (gameTip == null) {
                            reminderGames.add(game);
                        }
                    }

                    for (final Extra extra : nextExtras) {
                        final ExtraTip extraTip = ExtraTip.find("byExtraAndUser", extra, user).first();
                        if (extraTip == null) {
                            reminderBonus.add(extra);
                        }
                    }

                    if ((reminderGames.size() > 0) || (reminderBonus.size() > 0)) {
                        Mails.reminder(user, reminderGames, reminderBonus);
                        Logger.info("Reminder send to: " + user.getEmail());
                    }
                }
				
				for (final Game game : nextGames) {
					game.setReminder(true);
					game._save();
                }

                for (final Extra extra : nextExtras) {
					extra.setReminder(true);
					extra._save();
				}
				
                Logger.info("Finshed Job: ReminderJob");
            }
        }
    }
}