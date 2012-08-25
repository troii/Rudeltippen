package jobs;

import utils.AppUtils;

public class CalculationJob extends AppJob{
	@Override
	public void doJob() {
		AppUtils.calculations();
	}
}