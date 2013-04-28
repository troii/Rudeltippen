package jobs;

import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.jobs.On;
import services.MailService;
import services.TwitterService;
import utils.AppUtils;
import utils.NotificationUtils;

@On("0 0 3 * * ?")
public class StandingsJob extends AppJob {

	public StandingsJob() {
		this.setDescription("Sends the current Top 3 to every user who has this notification activated.");
		this.setExecuted("Runs daily at 03:00");
	}

	@Override
	public void doJob() {
		if (AppUtils.isJobInstance()) {
			Logger.info("Running job: StandingsJob");

			String message = "";
			final Game game = Game.find("byNumber", 1).first();
			if ((game != null) && game.isEnded()) {
				int count = 1;
				final StringBuilder buffer = new StringBuilder();

				List<User> users = User.find("ORDER BY place ASC").fetch(3);
				for (final User user : users) {
					if (count < 3) {
						buffer.append(user.getNickname() + " (" + user.getPoints() + " " + Messages.get("points") + "), ");
					} else {
						buffer.append(user.getNickname() + " (" + user.getPoints() + " " + Messages.get("points") + ")");
					}
					count++;
				}
				message = Messages.get("topthree") + ": " + buffer.toString();

				if (NotificationUtils.isTweetable()) {
					TwitterService.updateStatus(message);
				}

				users = User.find("bySendStandings", true).fetch();
				for (final User user : users) {
					MailService.notifications(Messages.get("mails.top3.subject"), message, user);
				}
			}
		}
	}
}