package utils;

import java.util.List;

import models.ConfirmationType;
import models.Extra;
import models.Game;
import models.Settings;
import models.User;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.mvc.Mailer;

public class MailUtils extends Mailer {

    public static void reminder(final User user, final List<Game> games, final List<Extra> extras) {
        final Settings settings = AppUtils.getSettings();
        final String replyto = Play.configuration.getProperty("mailservice.replyto");
        final String from = Play.configuration.getProperty("mailservice.from");
        final String recipient = user.getEmail();

        if (ValidationUtils.isValidEmail(recipient)) {
            setFrom(from);
            setReplyTo(replyto);
            addRecipient(recipient);
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + Messages.get("mails.subject.reminder")));
            send(AppUtils.getMailTemplate("notifications"), user, games, settings, extras);
        } else {
            Logger.error("Tryed to sent reminder, but recipient was invalid.");
        }
    }

    public static void confirm(final User user, final String token, final ConfirmationType confirmationType) {
        final Settings settings = AppUtils.getSettings();
        final String appUrl = Play.configuration.getProperty("app.register.url");
        final String replyto = Play.configuration.getProperty("mailservice.replyto");
        final String from = Play.configuration.getProperty("mailservice.from");

        if ((user != null) && ValidationUtils.isValidEmail(user.getEmail()) && StringUtils.isNotBlank(token) && (confirmationType != null)) {
            String subject = "";
            String message = "";

            if (ConfirmationType.ACTIVATION.equals(confirmationType)) {
                subject = Messages.get("mails.subject.activate");
                message = Messages.get("mails.message.activate");
            } else if (ConfirmationType.CHANGEUSERNAME.equals(confirmationType)) {
                subject = Messages.get("mails.subject.changeusername");
                message = Messages.get("mails.message.changeusername");
            } else if (ConfirmationType.CHANGEUSERPASS.equals(confirmationType)) {
                subject = Messages.get("mails.subject.changeuserpass");
                message = Messages.get("mails.message.changeuserpass");
            } else if (ConfirmationType.NEWUSERPASS.equals(confirmationType)) {
                subject = Messages.get("mails.subject.forgotuserpass");
                message = Messages.get("mails.message.forgotuserpass");
            }

            setReplyTo(replyto);
            setFrom(from);
            addRecipient(user.getEmail());
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + subject));
            if (ConfirmationType.NEWUSERPASS.equals(confirmationType)) {
                send(AppUtils.getMailTemplate("password"), user, token, appUrl, StringEscapeUtils.unescapeHtml(message));
            } else {
                send(AppUtils.getMailTemplate("confirm"), user, token, appUrl, StringEscapeUtils.unescapeHtml(message));
            }
        } else {
            Logger.error("Tryed to sent confirmation e-mail, but user or confirmType was null or recipient e-mail was invalid.");
        }
    }

    public static void newuser(final User user, final User admin) {
        final Settings settings = AppUtils.getSettings();
        final String replyto = Play.configuration.getProperty("mailservice.replyto");
        final String from = Play.configuration.getProperty("mailservice.from");

        if (ValidationUtils.isValidEmail(admin.getEmail()) && (user != null)) {
            setFrom(from);
            setReplyTo(replyto);
            addRecipient(admin.getEmail());
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + Messages.get("mails.subject.newuser")));
            send(AppUtils.getMailTemplate("newuser"), user, settings);
        } else {
            Logger.error("Tryed to sent new user e-mail to admin, but recipient was invalid or user was null.");
        }
    }

    public static void error(final String response, final String recipient) {
        final Settings settings = AppUtils.getSettings();
        final String from = Play.configuration.getProperty("mailservice.from");
        final String replyto = Play.configuration.getProperty("mailservice.replyto");

        if (ValidationUtils.isValidEmail(recipient) && StringUtils.isNotBlank(response)) {
            setReplyTo(replyto);
            setFrom(from);
            addRecipient(recipient);
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + Messages.get("mails.subject.updatefailed")));
            send(AppUtils.getMailTemplate("error"), response);
        } else {
            Logger.error("Tryed to sent info on webservice, but recipient was invalid or response was null.");
        }
    }

    public static void notifications(final String subject, String notification, final User user) {
        final Settings settings = AppUtils.getSettings();
        final String from = Play.configuration.getProperty("mailservice.from");
        final String replyto = Play.configuration.getProperty("mailservice.replyto");
        notification = StringEscapeUtils.unescapeHtml(notification);

        if (ValidationUtils.isValidEmail(user.getEmail()) && StringUtils.isNotEmpty(notification)) {
            setReplyTo(replyto);
            setFrom(from);
            addRecipient(user.getEmail());
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + subject));
            send(AppUtils.getMailTemplate("notifications"), notification);
        } else {
            Logger.error("Tryed to sent result notification, but recipient was invalid or notification was null.");
        }
    }

    public static void sendGameTips(final User user, final List<Game> games) {
        final Settings settings = AppUtils.getSettings();
        final String from = Play.configuration.getProperty("mailservice.from");
        final String replyto = Play.configuration.getProperty("mailservice.replyto");

        if (ValidationUtils.isValidEmail(user.getEmail()) && (games.size() > 0)) {
            setReplyTo(replyto);
            setFrom(from);
            addRecipient(user.getEmail());
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + Messages.get("overview")));
            send(AppUtils.getMailTemplate("gametips"), games, user);
        } else {
            Logger.error("Tryed to sent gametips mail, but recipient was invalid or games list was empty.");
        }
    }

    public static void sendRudelmail(final String subject, final String message, final Object [] bbcRecipients, String recipient) {
        final Settings settings = AppUtils.getSettings();
        final String from = Play.configuration.getProperty("mailservice.from");
        final String replyto = Play.configuration.getProperty("mailservice.replyto");

        if (StringUtils.isNotBlank(subject) && StringUtils.isNotBlank(message) && (bbcRecipients != null)) {
            setReplyTo(replyto);
            setFrom(from);
            addRecipient(recipient);
            addBcc(bbcRecipients);
            setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getGameName() + "] " + subject));
            send(AppUtils.getMailTemplate("rudelmail"), message);
        } else {
            Logger.error("Tryed to sent rudelmail, but subject, messages or recipients was empty");
        }
    }
}