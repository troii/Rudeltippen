package jobs;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.i18n.Lang;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class StartupJob extends Job{
	public void doJob() {
		String defaultLanguage = Play.configuration.getProperty("default.language");
		if (StringUtils.isBlank(defaultLanguage)) {
			defaultLanguage = "de";
		}
		Lang.change(defaultLanguage);
	}
}