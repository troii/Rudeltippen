package controllers;

import interfaces.AppConstants;
import interfaces.CheckAccess;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import models.Confirmation;
import models.ConfirmationType;
import models.Settings;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.Play;
import play.data.validation.Validation;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.utils.Java;
import utils.AppUtils;
import utils.MailUtils;
import utils.ValidationUtils;

public class Auth extends Root implements AppConstants{
    @Before(unless={"login", "authenticate", "logout", "forgotten", "resend", "register", "create", "confirm", "password", "reset", "renew"})
    protected static void checkAccess() throws Throwable {
        AppUtils.setAppLanguage();

        if (!session.contains("username")) {
            flash.put("url", "/");
            login();
        }

        CheckAccess check = getActionAnnotation(CheckAccess.class);
        if (check != null) {
            check(check);
        }

        check = getControllerInheritedAnnotation(CheckAccess.class);
        if (check != null) {
            check(check);
        }
    }

    @Before
    protected static void registration() {
        final Settings settings = AppUtils.getSettings();
        if (settings == null) {
            renderArgs.put("isEnableRegistration", false);
        } else {
            renderArgs.put("isEnableRegistration", settings.isEnableRegistration());
        }
    }

    private static void check(final CheckAccess check) throws Throwable {
        for (final String profile : check.value()) {
            final boolean hasProfile = (Boolean) Security.invoke("check", profile);
            if (!hasProfile) {
                Security.invoke("onCheckFailed", profile);
            }
        }
    }

    public static void password(final String token) {
        final Confirmation confirmation = Confirmation.find("byToken", token).first();
        if (confirmation == null) {
            flash.put("warningmessage", Messages.get("controller.users.invalidtoken"));
            flash.keep();
            redirect("/auth/login");
        }

        render(token);
    }

    public static void reset(final String email) {
        if (ValidationUtils.verifyAuthenticity()) {
            checkAuthenticity();
        }

        validation.required(email);
        validation.isTrue(ValidationUtils.emailExists(email)).key("email").message("validation.emailNotExists");
        validation.email(email);

        if (validation.hasErrors()) {
            flash.put("errormessage", Messages.get("controller.auth.resenderror"));
            validation.keep();
            params.flash();
            flash.keep();
            forgotten();
        } else {
            final User user = User.find("byEmailAndActive", email, true).first();
            if (user != null) {
                final String token = Codec.UUID();
                final ConfirmationType confirmType = ConfirmationType.NEWUSERPASS;
                final Confirmation confirmation = new Confirmation();
                confirmation.setUser(user);
                confirmation.setToken(token);
                confirmation.setConfirmType(confirmType);
                confirmation.setConfirmValue(Crypto.encryptAES(Codec.UUID()));
                confirmation.setCreated(new Date());
                confirmation._save();

                MailUtils.confirm(user, token, confirmType);
                flash.put("infomessage", Messages.get("confirm.message"));
                flash.keep();
                login();
            }
        }
        redirect("/");
    }

    public static void confirm(final String token) throws Throwable {
        Confirmation confirmation = null;
        validation.required(token);
        validation.match(token, CONFIRMATIONPATTERN);

        if (validation.hasErrors()) {
            flash.put("warningmessage", Messages.get("controller.users.invalidtoken"));
        } else {
            confirmation = Confirmation.find("byToken", token).first();
        }

        if ((confirmation != null) && !validation.hasErrors()) {
            final User user = confirmation.getUser();
            if (user != null) {
                final ConfirmationType confirmationType = confirmation.getConfirmType();
                doConfirmation(confirmation, user, confirmationType);
            } else {
                flash.put("warningmessage", Messages.get("controller.users.invalidtoken"));
            }
        } else {
            flash.put("warningmessage", Messages.get("controller.users.invalidtoken"));
        }
        flash.keep();
        redirect("/auth/login");
    }

    private static void doConfirmation(final Confirmation confirmation, final User user, final ConfirmationType confirmationType) {
        if ((ConfirmationType.ACTIVATION).equals(confirmationType)) {
            activateAndSetAvatar(user);
            Logger.info("User activated: " + user.getEmail());
            flash.put("infomessage", Messages.get("controller.users.accountactivated"));
        } else if ((ConfirmationType.CHANGEUSERNAME).equals(confirmationType)) {
            final String oldusername = user.getEmail();
            final String newusername = Crypto.decryptAES(confirmation.getConfirmValue());
            user.setEmail(newusername);
            user._save();
            session.remove("username");
            Logger.info("User changed username... old username: " + oldusername + " - " + "new username: " + newusername);
            flash.put("infomessage", Messages.get("controller.users.changedusername"));
        } else if ((ConfirmationType.CHANGEUSERPASS).equals(confirmationType)) {
            user.setUserpass(Crypto.decryptAES(confirmation.getConfirmValue()));
            user._save();
            session.remove("username");
            Logger.info(user.getEmail() + " changed his password");
            flash.put("infomessage", Messages.get("controller.users.changeduserpass"));
        }
        confirmation._delete();
    }

    private static void activateAndSetAvatar(final User user) {
        final String avatar = AppUtils.getGravatarImage(user.getEmail(), "retro", PICTURELARGE);
        final String avatarSmall = AppUtils.getGravatarImage(user.getEmail(), "retro", PICTURESMALL);
        if (StringUtils.isNotBlank(avatar)) {
            user.setPictureLarge(avatar);
        }
        if (StringUtils.isNotBlank(avatarSmall)) {
            user.setPicture(avatarSmall);
        }

        user.setActive(true);
        user._save();
    }

    @Transactional(readOnly=true)
    public static void register() {
        final Settings settings = AppUtils.getSettings();
        if (!settings.isEnableRegistration()) {
            redirect("/");
        }

        render();
    }

    public static void create(final String username, final String email, final String emailConfirmation, final String userpass, final String userpassConfirmation) {
        if (ValidationUtils.verifyAuthenticity()) { checkAuthenticity(); }

        final Settings settings = AppUtils.getSettings();
        if (!settings.isEnableRegistration()) {
            redirect("/");
        }

        validation.required(email);
        validation.required(userpass);
        validation.required(username);
        validation.email(email);
        validation.equals(email, emailConfirmation);
        validation.equals(userpass, userpassConfirmation);
        validation.minSize(userpass, 8);
        validation.maxSize(userpass, 32);
        validation.minSize(username, 3);
        validation.maxSize(username, 20);
        validation.isTrue(ValidationUtils.isValidUsername(username)).key("username").message(Messages.get("controller.users.invalidusername"));
        validation.isTrue(!ValidationUtils.usernameExists(username)).key("username").message(Messages.get("controller.users.usernamexists"));
        validation.isTrue(!ValidationUtils.emailExists(email)).key("email").message(Messages.get("controller.users.emailexists"));

        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            register();
        } else {
            final String salt = Codec.hexSHA1(Codec.UUID());
            final User user = new User();
            user.setRegistered(new Date());
            user.setUsername(username);
            user.setEmail(email);
            user.setActive(false);
            user.setReminder(true);
            user.setAdmin(false);
            user.setSalt(salt);
            user.setUserpass(AppUtils.hashPassword(userpass, salt));
            user.setPoints(0);
            user._save();

            final String token = Codec.UUID();
            final ConfirmationType confirmationType = ConfirmationType.ACTIVATION;
            final Confirmation confirmation = new Confirmation();
            confirmation.setConfirmType(confirmationType);
            confirmation.setConfirmValue(Crypto.encryptAES(Codec.UUID()));
            confirmation.setCreated(new Date());
            confirmation.setToken(token);
            confirmation.setUser(user);
            confirmation._save();

            MailUtils.confirm(user, token, confirmationType);
            if (settings.isInformOnNewTipper()) {
                final List<User> admins = User.find("byAdmin", true).fetch();
                for (final User admin : admins) {
                    MailUtils.newuser(user, admin);
                }
            }
            Logger.info("User registered: " + user.getEmail());
        }
        render(settings);
    }

    public static void login() {
        if (session.contains("username")) {
            redirect("/application/index");
        }

        final Http.Cookie remember = request.cookies.get("rememberme");
        if ((remember != null) && (remember.value.indexOf("-") > 0)) {
            final String sign = remember.value.substring(0, remember.value.indexOf("-"));
            final String username = remember.value.substring(remember.value.indexOf("-") + 1);
            if ((sign != null) && (username != null) && Crypto.sign(username).equals(sign)) {
                session.put("username", username);
                redirectToOriginalURL();
            }
        }
        flash.keep("url");
        render();
    }

    @Transactional(readOnly=true)
    public static void forgotten() {
        render();
    }

    public static void renew(final String token, final String userpass, final String userpassConfirmation) {
        validation.required(token);
        validation.match(token, CONFIRMATIONPATTERN);
        validation.required(userpass);
        validation.equals(userpass, userpassConfirmation);
        validation.minSize(userpass, 8);
        validation.maxSize(userpass, 32);

        final Confirmation confirmation = Confirmation.find("byToken", token).first();
        if (confirmation == null) {
            flash.put("warningmessage", Messages.get("controller.users.invalidtoken"));
            flash.keep();
            redirect("/auth/login");
        }

        if (validation.hasErrors()) {
            Validation.keep();
            password(token);
        } else {
            final User user = confirmation.getUser();
            final String password = AppUtils.hashPassword(userpass, user.getSalt());
            user.setUserpass(password);
            user._save();

            confirmation._delete();
            flash.put("infomessage", Messages.get("controller.auth.passwordreset"));
            flash.keep();
            redirect("/auth/login");
        }
    }

    public static void authenticate(final String username, final String userpass, final boolean remember) {
        if (ValidationUtils.verifyAuthenticity()) { checkAuthenticity(); }

        Boolean allowed = false;
        try {
            allowed = (Boolean) Security.invoke("authenticate", username, userpass);
            validation.isTrue(allowed);
            validation.required(username);
            validation.required(userpass);
        } catch (final UnsupportedOperationException e) {
            Logger.error("UnsupportedOperationException while authenticating", e);
        } catch (final Throwable e) {
            Logger.error("Authentication exception", e);
        }

        if (!allowed || validation.hasErrors()) {
            flash.keep("url");
            flash.put("errormessage", Messages.get("validation.invalidLogin"));
            params.flash();
            Validation.keep();
            login();
        } else {
            session.put("username", username);
            if (remember) {
                response.setCookie("rememberme", Crypto.sign(username) + "-" + username, "7d");
            }
        }

        redirectToOriginalURL();
    }

    @Transactional(readOnly=true)
    public static void logout() throws Throwable {
        Security.invoke("onDisconnected");
        session.clear();
        response.removeCookie("rememberme");

        flash.put("infomessage", Messages.get("controller.auth.logout"));
        flash.keep();

        login();
    }

    static void redirectToOriginalURL() {
        try {
            Security.invoke("onAuthenticated");
        } catch (final Throwable e) {
            Logger.error("Failed to onvoke onAuthenticated", e);
        }
        String url = flash.get("url");

        if (StringUtils.isBlank(url)) {
            url = "/";
        }

        redirect(url);
    }

    public static class Security extends Controller {
        static boolean authenticate(final String username, final String userpass) {
            String usersalt = null;
            final User user = User.find("SELECT u FROM User u WHERE active = true AND username = ? OR email = ?", username, username).first();
            if (user != null) {
                usersalt = user.getSalt();
                return AppUtils.connectUser(username, AppUtils.hashPassword(userpass, usersalt)) != null;
            }

            return false;
        }

        static boolean check(final String profile) {
            boolean valid = false;
            final User user = User.find("SELECT u FROM User u WHERE active = true AND username = ? OR email = ?", connected(), connected()).first();
            if (user != null) {
                valid = user.isAdmin();
            }

            return valid;
        }

        public static String connected() {
            return session.get("username");
        }

        static boolean isConnected() {
            return session.contains("username");
        }

        static void onAuthenticated() {
            Logger.info("User logged in: " + Security.connected());
        }

        static void onDisconnected() {
            Logger.info("User logged out: " + Security.connected());
        }

        static void onCheckFailed(final String profile) {
            forbidden();
        }

        private static Object invoke(final String m, final Object... args) throws Throwable {
            Class security = null;
            final List<Class> classes = Play.classloader.getAssignableClasses(Security.class);
            if (classes.size() == 0) {
                security = Security.class;
            } else {
                security = classes.get(0);
            }
            try {
                return Java.invokeStaticOrParent(security, m, args);
            } catch (final InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}