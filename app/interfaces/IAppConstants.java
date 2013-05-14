package interfaces;

public interface IAppConstants {
	public static final String APPNAME = "rudeltippen";
	public static final String YAMLFILE = "bl2012.yml";
	public static final String DEFAULT_DATEFORMAT = "dd.MM.yyyy";
	public static final String DEFAULT_TIMEFORMAT = "kk:mm";
	public static final String DEFAULT_TIMEZONE = "Europe/Berlin";
	public static final String WS_ENCODING = "UTF-8";
	public static final String WS_CONTENT_TYPE = "application/soap+xml";
	public static final String WS_URL = "http://www.openligadb.de/Webservices/Sportsdata.asmx";
	public static final String CONFIRMATIONPATTERN = "\\w{8,8}-\\w{4,4}-\\w{4,4}-\\w{4,4}-\\w{12,12}";
	public static final String EMAILPATTERN = ".+@.+\\.[a-z]+";
	public static final String USERNAMEPATTERN = "[a-zA-Z0-9-_]+";
	public static final String TWITTER_API_URL = "https://api.twitter.com/1.1/statuses/update.json";
	public static final int PICTURESMALL = 64;
	public static final int PICTURELARGE = 128;
}