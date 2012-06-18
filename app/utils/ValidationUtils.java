package utils;

import interfaces.AppConstants;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Confirmation;
import models.User;

import org.apache.commons.lang.StringUtils;

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
	public static boolean usernameExists(String username) {
		List<Confirmation> confirmations = Confirmation.findAll();
		for (Confirmation confirmation : confirmations) {
			String value = confirmation.getConfirmValue();
			value = Crypto.decryptAES(value);

			if (value.equalsIgnoreCase(username)) {
				return true;
			}
		}

		User user = User.find("byUsername", username).first();
		return user != null;
	}

	/**
	 * Checks in the database if a nickname already exists
	 * 
	 * @param nickname The nickname to check
	 * @return true if nickname exists, false otherwise
	 */
	public static boolean nicknameExists(String nickname) {
		User user = User.find("byNickname", nickname).first();
		return user != null;
	}

	/**
	 * Checks if the given filesize is lower or equal than configured in application.conf
	 * 
	 * @param filesize The filesize to check
	 * @return true if filesiize is lower or equal given filesize, false otherwise
	 */
    public static boolean checkFileLength(Long filesize) {
    	if (filesize > 0 && (filesize <= AppUtils.getSettings().getMaxPictureSize())) {
    		return true;
        }
        return false;
    }

    /**
     * Checks if given homeScore and awayScore is castable to string and between 0 and 99
     * 
     * @param homeScore The homeScore to check
     * @param awayScore The awayScore to check
     * @return true if score is valid, false otherwise
     */
    public static boolean isValidScore(String homeScore, String awayScore) {
        if (StringUtils.isBlank(homeScore) || StringUtils.isBlank(awayScore)) {
            return false;
        }

        homeScore = homeScore.trim();
        awayScore = awayScore.trim();
        int home, away;
        try {
            home = Integer.parseInt(homeScore);
            away = Integer.parseInt(awayScore);
        } catch (Exception e) {
        	e.printStackTrace();
            return false;
        }

        if (home >= 0 && home <= 99 && away >= 0 && away <= 99) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a given email is matching defined EMAILPATTERN
     * 
     * @param email The email to check
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
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
    public static boolean isValidNickname(String nickname) {
        final Pattern p = Pattern.compile(USERNAMEPATTERN);
        final Matcher m = p.matcher(nickname);
        return m.matches();  	
    }
    
    public static Validation getSettingsValidations(
    			Validation validation,
    			String name,
				int pointsGameWin,
				int pointsGameDraw,
				int pointsTip,
				int pointsTipDiff,
				int pointsTipTrend,
				int minutesBeforeTip,
				int maxPictureSize,
				String timeZoneString,
				String dateString,
				String dateTimeLang,
				String timeString,
				boolean countFinalResult,
				boolean informOnNewTipper,
				boolean enableRegistration) {
    	
		validation.required(name);
		validation.required(timeZoneString);
		validation.required(dateString);
		validation.required(dateTimeLang);
		validation.required(timeString);
		validation.range(pointsGameDraw, 0, 99);
		validation.range(pointsGameWin, 1, 99);
		validation.range(pointsGameDraw, 0, 99);
		validation.range(pointsTip, 0, 99);
		validation.range(pointsTipDiff, 0, 99);
		validation.range(pointsTipTrend, 0, 99);
    	
    	return validation;
    }
}