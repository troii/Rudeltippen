package utils;

import java.util.List;

import models.Game;
import models.GameTip;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Messages;
import services.MailService;
import services.TwitterService;

public class NotificationUtils {
	/**
	 * Generates a notifcation message for a given game
	 *
	 * @param game The game
	 * @return The message
	 */
	public static String getTwitterNotificationMessage(final Game game) {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(Messages.get("helper.tweetscore"));
		buffer.append(" ");
		buffer.append(Messages.get(game.getHomeTeam().getName()));
		buffer.append(" - ");
		buffer.append(Messages.get(game.getAwayTeam().getName()));
		buffer.append(" ");
		if (game.isOvertime()) {
			buffer.append(game.getHomeScoreOT());
			buffer.append(":");
			buffer.append(game.getAwayScoreOT());
			buffer.append(" (" + Messages.get(game.getOvertimeType()) + ")");
		} else {
			buffer.append(game.getHomeScore());
			buffer.append(":");
			buffer.append(game.getAwayScore());
		}
		buffer.append(" - " + Messages.get(game.getPlayday().getName()));

		return buffer.toString();
	}

	/**
	 * Generates a notifcation message for a given game
	 *
	 * @param game The game
	 * @return The message
	 */
	public static String getEmailNotificationMessage(final User user, final Game game) {
		final StringBuilder buffer = new StringBuilder();
		final GameTip gameTip = GameTip.find("byUserAndGame", user, game).first();

		buffer.append(Messages.get("helper.tweetscore"));
		buffer.append(" ");
		buffer.append(Messages.get(game.getHomeTeam().getName()));
		buffer.append(" - ");
		buffer.append(Messages.get(game.getAwayTeam().getName()));
		buffer.append(" ");
		if (game.isOvertime()) {
			buffer.append(game.getHomeScoreOT());
			buffer.append(":");
			buffer.append(game.getAwayScoreOT());
			buffer.append(" (" + Messages.get(game.getOvertimeType()) + ")");
		} else {
			buffer.append(game.getHomeScore());
			buffer.append(":");
			buffer.append(game.getAwayScore());
		}
		buffer.append(" - " + Messages.get(game.getPlayday().getName()));
		buffer.append("\n\n");
		buffer.append(Messages.get("yourbet") + " " + gameTip.getHomeScore() + " : " + gameTip.getAwayScore());

		return buffer.toString();
	}

	/**
	 * Checks if in current instance twitter configuration is enabled
	 *
	 * @return true if enabled, false otherwise
	 */
	public static boolean isTweetable() {
		boolean isTweetable = false;
		final String tweetable = Play.configuration.getProperty("twitter.enable");
		if (StringUtils.isNotBlank(tweetable) && "true".equals(tweetable)) {
			isTweetable = true;
		}

		return isTweetable;
	}

	/**
	 * Sends notification to twitter and every user who wants to be informed on new results
	 * @param game The game object
	 */
	public static void sendNotfications(final Game game) {
		if (!game.isEnded()) {
			TwitterService.updateStatus(NotificationUtils.getTwitterNotificationMessage(game));

			final List<User> users = User.find("byNotification", true).fetch();
			for (final User user : users) {
				MailService.notifications(Messages.get("mails.subject.notification"), NotificationUtils.getEmailNotificationMessage(user, game), user);
			}
		}
	}
}
