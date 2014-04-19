package unit.utils;

import jobs.CleanupJob;
import jobs.ReminderJob;
import jobs.ResultsJob;

import org.junit.Test;

import play.test.UnitTest;

public class JobTests extends UnitTest {
    @Test
    public void testJobs() {
        new CleanupJob().now();
        new ReminderJob().now();
        new ResultsJob().now();
    }
}