package utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Confirmation;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Crypto;

public class ValidationUtils {
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

	public static boolean nicknameExists(String nickname) {
		User user = User.find("byNickname", nickname).first();
		return user != null;
	}

    public static boolean checkFileLength(Long filesize) {
    	if (filesize > 0 && (filesize <= AppUtils.getSettings().getMaxPictureSize())) {
    		return true;
        }
        return false;
    }

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

    public static boolean isValidEmail(String email) {
        final Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        final Matcher m = p.matcher(email);
        return m.matches();
    }

    public static Validation getUserValidations(
    		Validation validation,
    		String username,
    		String userpass,
    		String nickname,
    		String usernameConfirmation,
    		String userpassConfirmation,
    		boolean update) {

		validation.required(username);
		validation.required(userpass);
		validation.required(nickname);
		validation.email(username);
		validation.equals(username, usernameConfirmation);
		validation.equals(userpass, userpassConfirmation);
		validation.minSize(userpass, 8);
		validation.maxSize(userpass, 32);
		validation.minSize(nickname, 3);
		validation.maxSize(nickname, 20);
		if (!update) {
			validation.isTrue(!ValidationUtils.nicknameExists(nickname)).key("nickname").message(Messages.get("controller.users.nicknamexists"));
			validation.isTrue(!ValidationUtils.usernameExists(username)).key("username").message(Messages.get("controller.users.emailexists"));
		}

		return validation;
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