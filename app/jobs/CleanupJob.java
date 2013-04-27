package jobs;

import java.util.List;

import models.Confirmation;
import models.ExtraTip;
import models.GameTip;
import models.User;
import models.enums.ConfirmationType;
import play.Logger;
import play.jobs.On;
import utils.AppUtils;

@On("0 0 2 * * ?")
public class CleanupJob extends AppJob {

	public CleanupJob() {
		this.setDescription("Cleans up the database by removing users which did not activate their account within 48 hours after registration.");
		this.setExecuted("Runs daily at 02:00");
	}

	@Override
	public void doJob() {
		if (AppUtils.isJobInstance()) {
			Logger.info("Running job: CleanupJob");
			final List<Confirmation> confirmations = Confirmation.find("SELECT c FROM Confirmation c WHERE confirmType = ? AND DATE(NOW()) > (DATE(created) + 2)", ConfirmationType.ACTIVATION).fetch();
			for (final Confirmation confirmation : confirmations) {
				final User user = confirmation.getUser();
				if ((user != null) && !user.isActive()) {
					final List<GameTip> gameTips = user.getGameTips();
					final List<ExtraTip> extraTips = user.getExtraTips();
					if ( ((gameTips == null) || (gameTips.size() <= 0)) && ((extraTips == null) || (extraTips.size() <= 0)) ) {
						Logger.info("Deleting user: '" + user.getNickname() + " (" + user.getUsername() + ")' - User did not activate within 2 days after registration and has no game tips and no extra tips.");
						user._delete();
					}
				}
			}
		}
	}
}