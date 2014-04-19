package jobs;

import java.util.ArrayList;
import java.util.List;

import models.AbstractJob;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class StartupJob extends AppJob{

    public StartupJob() {
        this.setDescription("Loads the Jobs which are active or inactive");
        this.setExecuted("Runs at application start");
    }

    @Override
    public void doJob() {
        List<String> jobNames = new ArrayList<String>();
        jobNames.add("CleanupJob");
        jobNames.add("GameTipJob");
        jobNames.add("PlaydayJob");
        jobNames.add("ReminderJob");
        jobNames.add("ResultsJob");

        for (String jobName : jobNames) {
            AbstractJob abstractJob = AbstractJob.find("byName", jobName).first();
            if (abstractJob == null) {
                abstractJob = new AbstractJob();
                abstractJob.setActive(true);
                abstractJob.setName(jobName);
                abstractJob._save();
            }
        }
    }
}