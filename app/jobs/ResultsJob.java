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
		if (AppUtils.isJobInstance()) {
	        Logger.info("Running job: ResultsJob");
		    List<Game> games = Game.find("SELECT g FROM Game g WHERE ended != 1 AND NOW() > kickoff AND homeTeam_id != '' AND awayTeam_id != '' AND webserviceID != ''").fetch();
			for (Game game : games) {
				final WSResults wsResults = UpdateService.setResultsFromWebService(game);
				if (wsResults != null) {
					AppUtils.setGameScoreFromWebService(game, wsResults);
				}
			}			
		}
	}
}