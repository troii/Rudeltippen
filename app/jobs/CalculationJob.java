package jobs;

import play.jobs.Job;
import utils.AppUtils;

public class CalculationJob extends Job{
	@Override
	public void doJob() {
		AppUtils.calculations();
	}
}