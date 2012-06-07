package jobs;

import java.util.ArrayList;
import java.util.List;

import models.Settings;
import models.User;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import play.Logger;
import play.db.DB;
import play.i18n.Messages;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.Crypto;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import services.MailService;
import utils.AppUtils;

@Every("60min")
public class UpdateJob extends Job{
	private static final String UPDATEURL = "http://api.rudeltippen.de";
	
	public void doJob() {
		if (AppUtils.isJobInstance() && AppUtils.isAutomaticUpdates()) {
			Logger.info("Running job: UpdateJob");
			Settings settings = AppUtils.getSettings();
			String dbName = settings.getDbName();
			int dbVersion = settings.getDbVersion();
			
			//FIXME Workaround for empty dbName when automatic update was not available
			if (StringUtils.isBlank(dbName)) {
				dbName = "em2012";
			}
			
			int latest = getLatestDbVersion(dbName);
			if (latest > dbVersion) {
				HttpResponse response = WS.url(UPDATEURL + "/updates/" + dbName + "/" + dbVersion + "/" + latest).get();
				if (response.success()) {
					JsonElement jsonElement = response.getJson();
					JsonArray jsonArray = jsonElement.getAsJsonArray();
					List<String> statements = new ArrayList<String>();
					for (int i=0; i < jsonArray.size(); i++) {
						JsonObject object = (JsonObject) jsonArray.get(i);
						JsonElement element = object.get("query");
						if (element != null) {
							String query = element.getAsString();
							if (StringUtils.isNotBlank(query)) {
								DB.execute(query);
							}
						}
					}
					
					settings.setDbVersion(latest);
					settings._save();

					List<User> admins = User.find("byAdmin", true).fetch();
					for (User user : admins) {
						MailService.updates(user, statements);
					}
				}				
			}
		}
	}

	private int getLatestDbVersion(String name) {
		HttpResponse response = WS.url(UPDATEURL + "/updates/latest/" + name).get();
		if (response.success()) {
			JsonElement jsonElement = response.getJson();
			if (jsonElement != null) {
				return jsonElement.getAsInt();
			}
		}
		
		return 0;
	}	
}