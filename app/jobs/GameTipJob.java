package jobs;

import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.jobs.Every;
import utils.AppUtils;
import utils.MailUtils;

@Every("1min")
public class GameTipJob extends AppJob{

    public GameTipJob() {
        this.setDescription("Sends an email once to every activated user, containing all tips from all users for started games.");
        this.setExecuted("Runs every minute");
    }

    @Override
    public void doJob() {
        if (AppUtils.isJobInstance()) {
            Logger.info("Started Job: GameTipJob");
            final List<User> users = User.find("SELECT u FROM User u WHER active = ? AND sendGameTips = ?", true, true).fetch();
            final List<Game> games = Game.find("SELECT g FROM Game g WHERE informed = ? AND ( TIMESTAMPDIFF(MINUTE,kickoff,now()) > 1 )", false).fetch();
            
            if (games != null && games.size() > 0) {
                for (final User user : users) {
                    MailUtils.sendGameTips(user, games);
                }

                for (final Game game : games) {
                    game.setInformed(true);
                    game._save();
                }            	
            }
            
            Logger.info("Finished Job: GameTipJob");
        }
    }
}