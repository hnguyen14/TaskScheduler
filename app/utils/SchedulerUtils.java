package utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import models.TimerJob;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

public class SchedulerUtils {
	private static Scheduler instance;

	private static void initializeScheduler() {
		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			instance = sf.getScheduler();
			instance.start();
		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	public static List<TimerJob> getAlljob() throws SchedulerException {
		if (instance == null)
			initializeScheduler();
		List<TimerJob> retVal = new ArrayList<TimerJob>();
		List<String> groups = instance.getJobGroupNames();
		for (String groupName: groups) {
			for(JobKey jobKey: instance.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				JobDetail jd = instance.getJobDetail(jobKey);
				retVal.add(new TimerJob(jobKey.getName(), jd.getClass().getCanonicalName(), "", "", ""));
			}

		}
		return retVal;
	}
	
	public static void scheduleJob(TimerJob job) throws SchedulerException, ClassNotFoundException, ParseException {
		if (instance == null)
			initializeScheduler();

		JobDetail jd = job.buildJobDetail();
		Trigger trigger = job.buildJobTrigger();

		instance.scheduleJob(jd, trigger);
	}
}
