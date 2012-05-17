package services;

import java.util.List;

import models.ConfirmationType;
import models.Extra;
import models.Game;
import models.Settings;
import models.User;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.mvc.Mailer;
import utils.AppUtils;
import utils.ValidationUtils;

public class MailService extends Mailer {
	public static void register(User user) {
		final String appUrl = Play.configuration.getProperty("app.register.url");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");
		final String from = Play.configuration.getProperty("mailservice.from");
		final Settings settings = AppUtils.getSettings();
		final String recipient = user.getUsername();

		if (ValidationUtils.isValidEmail(recipient)) {
			setFrom(from);
			addRecipient(recipient);
			setReplyTo(replyto);
			setSubject("[" + settings.getName() + "] " + Messages.get("mails.subject.registration"));
			send(user, settings, appUrl);
		} else {
			Logger.error("Tryed to sent registration mail, but recipient was invalid.");
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
			setSubject("[" + settings.getName() + "] " + Messages.get("mails.subject.reminder"));
			send(user, games, settings, extras);
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
			setSubject("[" + settings.getName() + "] " + subject);
			send(user, token, appUrl, message);
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
			setSubject("[" + settings.getName() + "] " + Messages.get("mails.subject.newpassword"));
			send(user, userpass, appUrl);
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
			setSubject("[" + settings.getName() + "] " + Messages.get("mails.subject.newuser"));
			send(user, settings);
		} else {
			Logger.error("Tryed to sent new user e-mail to admin, but recipient was invalid.");
		}
	}

	public static void webserviceUpdateFailed(String response) {
		final Settings settings = AppUtils.getSettings();
		final String from = Play.configuration.getProperty("mailservice.from");
		final String replyto = Play.configuration.getProperty("mailservice.replyto");

		List<User> users = User.find("Admin", true).fetch();
		for (User user : users) {
			if (ValidationUtils.isValidEmail(user.getUsername())) {
				setReplyTo(replyto);
				setFrom(from);
				addRecipient(user.getUsername());
				setSubject("[" + settings.getName() + "] " + Messages.get("mails.subject.updatefailed"));
				send(response);
			} else {
				Logger.error("Tryed to sent info on webservice, but recipient was invalid.");
			}
		}
	}
}