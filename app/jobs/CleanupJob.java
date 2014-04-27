package jobs;

import java.util.List;

import models.AbstractJob;
import models.Confirmation;
import models.ConfirmationType;
import models.ExtraTip;
import models.GameTip;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.jobs.On;
import utils.AppUtils;

@On("0 0 2 * * ?")
public class CleanupJob extends AppJob {

    public CleanupJob() {
        this.setDescription(Messages.get("job.cleanupjob.description"));
        this.setExecuted(Messages.get("job.cleanupjob.executed"));
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            AbstractJob job = AbstractJob.find("byName", "CleanupJob").first();
            if (job != null && job.isActive()) {
                Logger.info("Started Job: CleanupJob");
                final List<Confirmation> confirmations = Confirmation.find("SELECT c FROM Confirmation c WHERE confirmType = ? AND DATE(NOW()) > (DATE(created) + 2)", ConfirmationType.ACTIVATION).fetch();
                for (final Confirmation confirmation : confirmations) {
                    final User user = confirmation.getUser();
                    if ((user != null) && !user.isActive()) {
                        final List<GameTip> gameTips = user.getGameTips();
                        final List<ExtraTip> extraTips = user.getExtraTips();
                        if ( ((gameTips == null) || (gameTips.size() <= 0)) && ((extraTips == null) || (extraTips.size() <= 0)) ) {
                            Logger.info("Deleting user: '" + user.getUsername() + " (" + user.getEmail() + ")' - User did not activate within 2 days after registration and has no game tips and no extra tips.");
                            user._delete();
                        }
                    }
                }
                Logger.info("Finished Job: CleanupJob");
            }
        }
    }
}