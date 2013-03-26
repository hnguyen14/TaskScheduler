package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import play.db.ebean.Model;

public class TimerJob extends Model{
	private static final long serialVersionUID = 710846763634438062L;

	public String jobName;
	public String jobClass;
	public String jobParams;
	public String jobStartTime;
	public String jobInterval;

	public TimerJob(String jobName, String jobClass, String jobParams, String jobStartTime, String jobInterval) {
		this.jobName = jobName;
		this.jobClass = jobClass;
		this.jobParams = jobParams;
		this.jobStartTime = jobStartTime;
		this.jobInterval = jobInterval;
	}

	public JobDetail buildJobDetail() 
			throws ClassNotFoundException{
		Class classObject = Class.forName(jobClass);
		String[] params = jobParams.split("\\|");
		JobDataMap jdm = new JobDataMap();
		for (String s: params) {
			String[] toks = s.split("=");
			jdm.put(toks[0], toks[1]);
		}
		JobDetail jd = JobBuilder.newJob(classObject)
				.withIdentity(jobName)
				.usingJobData(jdm)
				.build();
		return jd;
	}

	public Trigger buildJobTrigger() 
			throws NumberFormatException, ParseException{
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		TriggerBuilder<Trigger> trigger = TriggerBuilder.newTrigger()
				.startAt(df.parse(jobStartTime));

		int interval = Integer.parseInt(jobInterval);
		if (interval > 0) {
			trigger.withSchedule(
					SimpleScheduleBuilder.simpleSchedule()
					.withIntervalInSeconds(Integer.parseInt(jobInterval))
					.repeatForever());
		}

		return trigger.build();

	}
}
