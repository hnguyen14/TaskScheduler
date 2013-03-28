package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.TimerJob;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
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
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		for (String groupName: groups) {
			for(JobKey jobKey: instance.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				JobDetail jd = instance.getJobDetail(jobKey);
				Trigger trigger = instance.getTrigger(new TriggerKey(jobKey.getName() + "_trigger"));
				Date nextStart = trigger.getNextFireTime();
				if (nextStart == null) {
					nextStart = trigger.getStartTime();
				}
				String paramStr = "";
				JobDataMap jdm = jd.getJobDataMap();
				for(String key: jdm.getKeys()) {
					paramStr += key + "=" + jdm.getString(key) + "|";
				}
				if (paramStr.length() > 1) {
					paramStr = paramStr.substring(0, paramStr.length() - 1);
				}
				retVal.add(new TimerJob(
						jobKey.getName(), 
						jd.getJobClass().getCanonicalName(), 
						paramStr, 
						df.format(trigger.getNextFireTime()), 
						""));
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
