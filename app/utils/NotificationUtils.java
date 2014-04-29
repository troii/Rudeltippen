package utils;

import java.util.List;

import models.Game;
import models.GameTip;
import models.Playday;
import models.User;
import notifiers.Mails;
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
     * Sends notification to every user who wants to be informed on new results
     * @param game The game object
     */
    public static void sendNotfications(final Game game) {
        if (!game.isEnded()) {
            final List<User> users = User.find("byNotificationAndActive", true, true).fetch();
            for (final User user : users) {
                Mails.notifications(Messages.get("mails.subject.notification"), NotificationUtils.getEmailNotificationMessage(user, game), user);
            }
        }
    }

    /**
     * Sends the top three users to every user who is active and has
     * the "top 3" notification enabled
     */
    public static void sendTopThree(Playday playday) {
        String message = "";
        final Game game = Game.find("byNumber", 1).first();
        if ((game != null) && game.isEnded()) {
            int count = 1;
            final StringBuilder buffer = new StringBuilder();

            List<User> users = User.find("SELECT u FROM User u WHERE active = true ORDER BY place ASC").fetch(3);
            for (final User user : users) {
                if (count < 3) {
                    buffer.append(user.getUsername() + " (" + user.getPoints() + " " + Messages.get("points") + ")\n");
                } else {
                    buffer.append(user.getUsername() + " (" + user.getPoints() + " " + Messages.get("points") + ")");
                }
                count++;
            }
            message = Messages.get("topthree.notification", Messages.get(playday.getName())) + ": \n" + buffer.toString();

            users = User.find("bySendStandings", true).fetch();
            for (final User user : users) {
                Mails.notifications(Messages.get("mails.top3.subject"), message, user);
            }
        }
    }
}