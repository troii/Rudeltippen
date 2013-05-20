package jobs;

import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.jobs.Every;
import services.AppService;
import services.MailService;

@Every("1min")
public class GameTipJob extends AppJob{

    public GameTipJob() {
        this.setDescription("Sends a email to every activated user, containing all tips from all users for a specfic game or multiple games.");
        this.setExecuted("Runs every minute");
    }

    @Override
    public void doJob() {
        if (AppService.isJobInstance()) {
            Logger.info("Started Job: GameTipJob");
            final List<User> users = AppService.getAllActiveUsers();
            final List<Game> games = Game.find("SELECT g FROM Game g WHERE informed = ? AND ( TIMESTAMPDIFF(MINUTE,kickoff,now()) > 1 )", false).fetch();
            for (final User user : users) {
                MailService.sendGameTips(user, games);
            }

            for (final Game game : games) {
                game.setInformed(true);
                game._save();
            }

            Logger.info("Finished Job: GameTipJob");
        }
    }
}