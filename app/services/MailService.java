package services;

import java.util.List;

import models.ConfirmationType;
import models.Extra;
import models.Game;
import models.Settings;
import models.User;

import org.apache.commons.lang.StringEscapeUtils;

import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.mvc.Mailer;
import utils.AppUtils;
import utils.ValidationUtils;

public class MailService extends Mailer {
	public static void updates(User user, List<String> statements) {
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");
		final Settings settings = AppUtils.getSettings();
		final String recipient = user.getUsername();

		if (ValidationUtils.isValidEmail(recipient)) {
			setFrom(from);
			addRecipient(recipient);
			setReplyTo(replyto);
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.updates")));
			send(AppUtils.getMailTemplate("updates"), user, statements);
		} else {
			Logger.error("Tryed to sent updates mail, but recipient was invalid.");
		}
	}

	public static void reminder(User user, List<Game> games, List<Extra> extras) {
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

	public static void confirm(User user, String token, ConfirmationType confirmationType) {
		final Settings settings = AppUtils.getSettings();
		final String appUrl = Play.configuration.getProperty("app.register.url");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");

		if (user != null && ValidationUtils.isValidEmail(user.getUsername())) {
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
			Logger.error("Tryed to sent confirmation e-mail, but user was null or recipient e-mail invalid.");
		}
	}

	public static void newuserpass(User user, String userpass) {
		final Settings settings = AppUtils.getSettings();
		final String appUrl = Play.configuration.getProperty("app.register.url");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");

		String recipient = user.getUsername();
		if (ValidationUtils.isValidEmail(recipient)) {
			setReplyTo(replyto);
			setFrom(from);
			addRecipient(recipient);
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.newpassword")));
			send(AppUtils.getMailTemplate("newuserpass"), user, userpass, appUrl);
		} else {
			Logger.error("Tryed to sent new passwort, but recipient was invalid.");
		}
	}

	public static void newuser(User user, User admin) {
		final Settings settings = AppUtils.getSettings();
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");

		if (ValidationUtils.isValidEmail(admin.getUsername())) {
			setFrom(from);
			setReplyTo(replyto);
			addRecipient(admin.getUsername());
			setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.newuser")));
			send(AppUtils.getMailTemplate("newuser"), user, settings);
		} else {
			Logger.error("Tryed to sent new user e-mail to admin, but recipient was invalid.");
		}
	}

	public static void webserviceError(String response) {
		final Settings settings = AppUtils.getSettings();
		final String from = Play.configuration.getProperty("mailservice.from");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");

		List<User> users = User.find("Admin", true).fetch();
		for (User user : users) {
			if (ValidationUtils.isValidEmail(user.getUsername())) {
				setReplyTo(replyto);
				setFrom(from);
				addRecipient(user.getUsername());
				setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.updatefailed")));
				send(AppUtils.getMailTemplate("webserviceError"), response);
			} else {
				Logger.error("Tryed to sent info on webservice, but recipient was invalid.");
			}
		}
	}

	public static void notifications(String notification) {
		final Settings settings = AppUtils.getSettings();
		final String from = Play.configuration.getProperty("mailservice.from");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		notification = StringEscapeUtils.unescapeHtml(notification);
		
		List<User> users = User.find("byNotification", true).fetch();
		for (User user : users) {
			if (ValidationUtils.isValidEmail(user.getUsername())) {
				setReplyTo(replyto);
				setFrom(from);
				addRecipient(user.getUsername());
				setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.notification")));
				send(AppUtils.getMailTemplate("notifications"), notification);
			} else {
				Logger.error("Tryed to sent result notification, but recipient was invalid.");
			}	
		}
	}

	public static void sendStandings(String message) {
		final Settings settings = AppUtils.getSettings();
		final String from = Play.configuration.getProperty("mailservice.from");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		message = StringEscapeUtils.unescapeHtml(message);
		
		List<User> users = User.find("bySendStandings", true).fetch();
		for (User user : users) {
			if (ValidationUtils.isValidEmail(user.getUsername())) {
				setReplyTo(replyto);
				setFrom(from);
				addRecipient(user.getUsername());
				setSubject(StringEscapeUtils.unescapeHtml("[" + settings.getName() + "] " + Messages.get("mails.subject.standings")));
				send(AppUtils.getMailTemplate("notifications"), message);
			} else {
				Logger.error("Tryed to sent standings, but recipient was invalid.");
			}	
		}
	}
}