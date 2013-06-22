package utils;

import interfaces.AppConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Confirmation;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.libs.Crypto;

public class ValidationUtils implements AppConstants{
    /**
     * Checks in the database and pending confirmations if a given email already exists
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     */
    public static boolean emailExists(final String email) {
        boolean exists = false;
        final List<Confirmation> confirmations = Confirmation.findAll();
        for (final Confirmation confirmation : confirmations) {
            String value = confirmation.getConfirmValue();
            value = Crypto.decryptAES(value);

            if (value.equalsIgnoreCase(email)) {
                exists = true;
            }
        }

        if (!exists) {
            final User user = User.find("byEmail", email).first();
            if (user != null) {
                exists = true;
            }
        }

        return exists;
    }

    /**
     * Checks in the database if a username already exists
     * 
     * @param username The username to check
     * @return true if username exists, false otherwise
     */
    public static boolean usernameExists(final String username) {
        final User user = User.find("byUsername", username).first();
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
        if ((filesize > 0) && (filesize <= 102400)) {
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
     * Checks if a given username is matching defined USERNAMEPATTERN
     * 
     * @param username The username to check
     * @return true if username is valid, false otherwise
     */
    public static boolean isValidUsername(final String username) {
        final Pattern p = Pattern.compile(USERNAMEPATTERN);
        final Matcher m = p.matcher(username);
        return m.matches();
    }

    /**
     * Checks if application uses the authenticity token
     *
     * @return true if check.authenticity is set in application.conf, false otherwise
     */
    public static boolean verifyAuthenticity() {
        final String check = Play.configuration.getProperty("check.authenticity");
        boolean verify = false;

        if (!("false").equalsIgnoreCase(check)) {
            verify = true;
        }

        return verify;
    }
}