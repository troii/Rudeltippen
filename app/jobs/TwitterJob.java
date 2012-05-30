package jobs;

import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.jobs.Job;
import play.jobs.On;
import services.TwitterService;
import utils.AppUtils;

@On("0 0 3 * * ?")
public class TwitterJob extends Job{
	@Override
	public void doJob() {
		if (AppUtils.isJobInstance() && AppUtils.isTweetable()) {
		    Logger.info("Running job: TwitterJob");
		    final Game game = Game.find("byNumber", 1).first();
		    if (game != null && game.isEnded()) {
	            int count = 1;
	            StringBuilder buffer = new StringBuilder();

	            final List<User> users = User.find("ORDER BY points DESC").fetch(3);
	            for (User user : users) {
	                if (count < 3) {
	                    buffer.append(user.getNickname() + " (" + user.getPoints() + " " + Messages.get("points") + "), ");
	                } else {
	                    buffer.append(user.getNickname() + " (" + user.getPoints() + " " + Messages.get("points") + ")");
	                }
	                count++;
	            }
	            TwitterService.updateStatus(Messages.get("topthree") + ": " + buffer.toString());
		    }
		}
	}
}