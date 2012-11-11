package services;

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
import utils.AppUtils;
import utils.ValidationUtils;

public class MailService extends Mailer {

	public static void reminder(final User user, final List<Game> games, final List<Extra> extras) {
		final Settings settings = AppUtils.getSettings();
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");
		final String recipient = user.getUsername();

		if (ValidationUtils.isValidEmail(recipient)) {
			setFrom(from);
			setReplyTo(replyto);
			addRecipient(recipient);
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.reminder")));
			send(AppUtils.getMailTemplate("reminder"), user, games, settings, extras);
		} else {
			Logger.error("Tryed to sent reminder, but recipient was invalid.");
		}
	}

	public static void confirm(final User user, final String token, final ConfirmationType confirmationType) {
		final Settings settings = AppUtils.getSettings();
		final String appUrl = Play.configuration.getProperty("app.register.url");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");

		if ((user != null) && ValidationUtils.isValidEmail(user.getUsername()) && StringUtils.isNotBlank(token) && (confirmationType != null)) {
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
			} else if (ConfirmationType.FORGOTUSERPASS.equals(confirmationType)) {
				subject = Messages.get("mails.subject.forgotuserpass");
				message = Messages.get("mails.message.forgotuserpass");
			}

			setReplyTo(replyto);
			setFrom(from);
			addRecipient(user.getUsername());
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + subject));
			send(AppUtils.getMailTemplate("confirm"), user, token, appUrl, StringEscapeUtils.unescapeHtml(message));
		} else {
			Logger.error("Tryed to sent confirmation e-mail, but user or confirmType was null or recipient e-mail was invalid.");
		}
	}

	public static void newuserpass(final User user, final String userpass) {
		final Settings settings = AppUtils.getSettings();
		final String appUrl = Play.configuration.getProperty("app.register.url");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");

		final String recipient = user.getUsername();
		if (ValidationUtils.isValidEmail(recipient) && StringUtils.isNotBlank(userpass)) {
			setReplyTo(replyto);
			setFrom(from);
			addRecipient(recipient);
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.newpassword")));
			send(AppUtils.getMailTemplate("newuserpass"), user, userpass, appUrl);
		} else {
			Logger.error("Tryed to sent new passwort, but recipient was invalid or userpass was null.");
		}
	}

	public static void newuser(final User user, final User admin) {
		final Settings settings = AppUtils.getSettings();
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");

		if (ValidationUtils.isValidEmail(admin.getUsername()) && (user != null)) {
			setFrom(from);
			setReplyTo(replyto);
			addRecipient(admin.getUsername());
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.newuser")));
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
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.updatefailed")));
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

		if (ValidationUtils.isValidEmail(user.getUsername()) && StringUtils.isNotEmpty(notification)) {
			setReplyTo(replyto);
			setFrom(from);
			addRecipient(user.getUsername());
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + subject));
			send(AppUtils.getMailTemplate("notifications"), notification);
		} else {
			Logger.error("Tryed to sent result notification, but recipient was invalid or notification was null.");
		}
	}
}