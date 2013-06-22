package utils;

import java.util.List;

import models.Game;
import models.GameTip;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Messages;

public class NotificationUtils {

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
        
        if (gameTip != null) {
            buffer.append(Messages.get("yourbet") + " " + gameTip.getHomeScore() + " : " + gameTip.getAwayScore());
        }

        return buffer.toString();
    }

    /**
     * Sends notification to twitter and every user who wants to be informed on new results
     * @param game The game object
     */
    public static void sendNotfications(final Game game) {
        if (!game.isEnded()) {
            final List<User> users = User.find("byNotificationAndActive", true, true).fetch();
            for (final User user : users) {
                MailUtils.notifications(Messages.get("mails.subject.notification"), NotificationUtils.getEmailNotificationMessage(user, game), user);
            }
        }
    }
}
