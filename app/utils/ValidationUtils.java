package utils;

import interfaces.AppConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Confirmation;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Crypto;

public class ValidationUtils implements AppConstants{
	/**
	 * Checks in the database and pending confirmations if a given username already exists
	 * 
	 * @param username The username to check
	 * @return true if username exists, false otherwise
	 */
	public static boolean usernameExists(final String username) {
		boolean exists = false;
		final List<Confirmation> confirmations = Confirmation.findAll();
		for (final Confirmation confirmation : confirmations) {
			String value = confirmation.getConfirmValue();
			value = Crypto.decryptAES(value);

			if (value.equalsIgnoreCase(username)) {
				exists = true;
			}
		}

		if (!exists) {
			final User user = User.find("byUsername", username).first();
			if (user != null) {
				exists = true;
			}
		}

		return exists;
	}

	/**
	 * Checks in the database if a nickname already exists
	 * 
	 * @param nickname The nickname to check
	 * @return true if nickname exists, false otherwise
	 */
	public static boolean nicknameExists(final String nickname) {
		final User user = User.find("byNickname", nickname).first();
		return user != null;
	}

	/**
	 * Checks if the given filesize is lower or equal than configured in application.conf
	 * 
	 * @param filesize The filesize to check
	 * @return true if filesiize is lower or equal given filesize, false otherwise
	 */
	public static boolean checkFileLength(final Long filesize) {
		boolean check = false;
		if ((filesize > 0) && (filesize <= AppUtils.getSettings().getMaxPictureSize())) {
			check = true;
		}

		return check;
	}

	/**
	 * Checks if given homeScore and awayScore is castable to string and between 0 and 99
	 * 
	 * @param homeScore The homeScore to check
	 * @param awayScore The awayScore to check
	 * @return true if score is valid, false otherwise
	 */
	public static boolean isValidScore(String homeScore, String awayScore) {
		boolean valid = false;
		if (StringUtils.isNotBlank(homeScore) && StringUtils.isNotBlank(awayScore)) {
			homeScore = homeScore.trim();
			awayScore = awayScore.trim();
			int home, away;
			try {
				home = Integer.parseInt(homeScore);
				away = Integer.parseInt(awayScore);

				if ((home >= 0) && (home <= 99) && (away >= 0) && (away <= 99)) {
					valid = true;
				}
			} catch (final Exception e) {
				Logger.error("Invalid store given",  e);
			}
		}

		return valid;
	}

	/**
	 * Checks if a given email is matching defined EMAILPATTERN
	 * 
	 * @param email The email to check
	 * @return true if email is valid, false otherwise
	 */
	public static boolean isValidEmail(final String email) {
		final Pattern p = Pattern.compile(EMAILPATTERN);
		final Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * Checks if a given nickname is matching defined USERNAMEPATTERN
	 * 
	 * @param username The username to check
	 * @return true if username is valid, false otherwise
	 */
	public static boolean isValidNickname(final String nickname) {
		final Pattern p = Pattern.compile(USERNAMEPATTERN);
		final Matcher m = p.matcher(nickname);
		return m.matches();
	}

	/**
	 * Creates a vliadtion object with the given
	 * 
	 * @param validation
	 * @param tournament
	 * @param name
	 * @param pointsGameWin
	 * @param pointsGameDraw
	 * @param pointsTip
	 * @param pointsTipDiff
	 * @param pointsTipTrend
	 * @param minutesBeforeTip
	 * @param maxPictureSize
	 * @param timeZoneString
	 * @param dateString
	 * @param dateTimeLang
	 * @param timeString
	 * @param theme
	 * @param countFinalResult
	 * @param informOnNewTipper
	 * @param enableRegistration
	 * 
	 * @return The validatiob object
	 */
	public static Validation getSettingsValidations(
			final Validation validation,
			final String tournament,
			final String name,
			final int pointsGameWin,
			final int pointsGameDraw,
			final int pointsTip,
			final int pointsTipDiff,
			final int pointsTipTrend,
			final int minutesBeforeTip,
			final int maxPictureSize,
			final String timeZoneString,
			final String dateString,
			final String dateTimeLang,
			final String timeString,
			final String theme,
			final boolean countFinalResult,
			final boolean informOnNewTipper,
			final boolean enableRegistration) {

		validation.required(name);
		validation.required(timeZoneString);
		validation.required(dateString);
		validation.required(dateTimeLang);
		validation.required(timeString);
		validation.required(tournament);
		validation.range(pointsGameDraw, 0, 99);
		validation.range(pointsGameWin, 1, 99);
		validation.range(pointsGameDraw, 0, 99);
		validation.range(pointsTip, 0, 99);
		validation.range(pointsTipDiff, 0, 99);
		validation.range(pointsTipTrend, 0, 99);
		validation.isTrue(!("0").equals(tournament)).key("tournament").message(Messages.get("system.invalidtournament"));
		validation.isTrue(ValidationUtils.isValidTheme(theme)).key("theme").message(Messages.get("system.invalidtheme"));

		return validation;
	}

	/**
	 * Checks if a given theme exists
	 * 
	 * @param theme The name of the theme
	 * @return true if the theme exists, false otherwise
	 */
	public static boolean isValidTheme(final String theme) {
		boolean valid = false;
		final List<String> themes = ViewUtils.getThemes();
		if (themes.contains(theme)) {
			valid = true;
		}

		return valid;
	}
}