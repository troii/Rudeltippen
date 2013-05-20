package jobs;

import play.jobs.Every;
import services.AppService;

@Every("1min")
public class ResultsJob extends AppJob {

    public ResultsJob() {
        this.setDescription("Checks if games have started and if results for this games are available.");
        this.setExecuted("Runs every minute");
    }

    @Override
    public void doJob() {
        if (AppService.isJobInstance()) {
            //			Logger.info("Started Job: ResultsJob");
            //			final List<Game> games = Game.find("SELECT g FROM Game g WHERE ended != 1 AND ( TIMESTAMPDIFF(MINUTE,kickoff,now()) > 90 ) AND homeTeam_id != '' AND awayTeam_id != '' AND webserviceID != ''").fetch();
            //			for (final Game game : games) {
            //				final WSResults wsResults = UpdateService.getResultsFromWebService(game);
            //				if ((wsResults != null) && wsResults.isUpdated()) {
            //					AppUtils.setGameScoreFromWebService(game, wsResults);
            //				}
            //			}
            //          Logger.info("Finished Job: ResultsJob");
        }
    }
}