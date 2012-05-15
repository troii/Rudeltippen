package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.User;

import org.apache.commons.lang.StringUtils;

public class ValidationUtils {
	public static boolean usernameExists(String username) {
		User user = User.find("byUsername", username).first();
		return user != null;
	}

	public static boolean emailExists(String email) {
		User user = User.find("byEmail", email).first();
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
