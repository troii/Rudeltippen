package jobs;

import java.util.List;

import models.Game;
import models.WSResults;
import play.Logger;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import services.UpdateService;
import utils.AppUtils;

@Every("5min")
public class ResultsJob extends Job{
	@Override
	public void doJob() {
		final String autoupdate = Play.configuration.getProperty("app.autoupdate.results");
		if (("1").equals(autoupdate) && AppUtils.isJobInstance()) {
	        Logger.info("Running Job: UpdateResults");
		    List<Game> games = Game.find("SELECT g FROM Game g WHERE DATE(kickoff) = DATE(NOW()) AND NOW() > playTime AND homeTeam_id != '' AND awayTeam_id != '' AND webserviceID != '' AND ended != 1").fetch();
			for (Game game : games) {
				final WSResults wsResults = UpdateService.setResultsFromWebService(game);
				if (wsResults != null) {
					AppUtils.setGameScoreFromWebService(game, wsResults);
				}
			}			
		}
	}
}