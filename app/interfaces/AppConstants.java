package interfaces;

public interface AppConstants {
	String APPNAME = "rudeltippen";
	String YAMLFILE = "em2016.yml";
	String DEFAULT_DATEFORMAT = "dd.MM.yyyy";
	String DEFAULT_TIMEFORMAT = "kk:mm";
	String DEFAULT_TIMEZONE = "Europe/Berlin";
	String WS_ENCODING = "UTF-8";
	String WS_CONTENT_TYPE = "application/soap+xml";
	String WS_URL = "http://www.openligadb.de/Webservices/Sportsdata.asmx";
	String CONFIRMATIONPATTERN = "\\w{8,8}-\\w{4,4}-\\w{4,4}-\\w{4,4}-\\w{12,12}";
	String EMAILPATTERN = ".+@.+\\.[a-z]+";
	String USERNAMEPATTERN = "[a-zA-Z0-9-_]+";
	int PICTURESMALL = 64;
	int PICTURELARGE = 128;
}