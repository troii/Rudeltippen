package jobs;

import interfaces.AppConstants;

import java.util.ArrayList;
import java.util.List;

import models.Settings;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.db.DB;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import services.MailService;
import utils.AppUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Every("60min")
public class UpdateJob extends Job implements AppConstants{
	@Override
	public void doJob() {
		if (AppUtils.isJobInstance() && AppUtils.automaticUpdates()) {
			Logger.info("Running job: UpdateJob");

			final Settings settings = AppUtils.getSettings();
			final String dbName = settings.getDbName();
			final int dbVersion = settings.getDbVersion();
			final int latest = getLatestDbVersion(dbName);

			if (StringUtils.isNotBlank(dbName) && (latest > dbVersion)) {
				final HttpResponse response = WS.url(APIURL + "/updates/" + dbName + "/" + dbVersion + "/" + latest).get();
				if (response.success()) {
					final JsonElement jsonElement = response.getJson();
					if (jsonElement != null) {
						final JsonArray jsonArray = jsonElement.getAsJsonArray();
						if ((jsonArray != null) && (jsonArray.size() > 0)) {
							final List<String> statements = new ArrayList<String>();
							for (int i=0; i < jsonArray.size(); i++) {
								final JsonObject object = (JsonObject) jsonArray.get(i);
								final JsonElement element = object.get("query");
								if (element != null) {
									final String query = element.getAsString();
									if (StringUtils.isNotBlank(query)) {
										DB.execute(query);
										statements.add(query);
										Logger.info("Executed SQL statement: " + query);
									}
								}
							}

							settings.setDbVersion(latest);
							settings._save();

							final List<User> admins = User.find("byAdmin", true).fetch();
							for (final User user : admins) {
								MailService.updates(user, statements);
							}
						}
					}
				}
			}
		}
	}

	private int getLatestDbVersion(final String name) {
		final HttpResponse response = WS.url(APIURL + "/updates/latest/" + name).get();
		if (response.success()) {
			final JsonElement jsonElement = response.getJson();
			if (jsonElement != null) {
				return jsonElement.getAsInt();
			}
		}

		return 0;
	}
}