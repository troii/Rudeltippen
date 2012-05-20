package utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Confirmation;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.libs.Crypto;

public class ValidationUtils {
	public static boolean usernameExists(String username) {
		List<Confirmation> confirmations = Confirmation.findAll();
		for (Confirmation confirmation : confirmations) {
			String value = confirmation.getConfirmValue();
			value = Crypto.decryptAES(value);

			if (value.equals(username)) {
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
}