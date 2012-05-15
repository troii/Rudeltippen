package jobs;

import java.util.List;

import models.Confirmation;
import models.ConfirmationType;
import models.User;
import play.Logger;
import play.jobs.Job;
import play.jobs.On;
import utils.AppUtils;

@On("0 0 2 * * ?")
public class CleanupJob extends Job {
	@Override
	public void doJob() {
		if (AppUtils.isJobInstance()) {
		    Logger.info("Running Job: Cleanup");
			List<Confirmation> confirmations = Confirmation.find("SELECT c FROM Confirmation c WHERE DATE(NOW()) > DATE(created) + 2").fetch();
			for (Confirmation confirmation : confirmations) {
				if (ConfirmationType.ACTIVATION.equals(confirmation.getConfirmType())) {
					User user = confirmation.getUser();
					Logger.info("Deleting User: '" + user.getNickname() + " (" + user.getUsername() + ")' - User did not activate within 48 Hours.");
					confirmation._delete();
					user._delete();
				}
			}
		}
	}
}