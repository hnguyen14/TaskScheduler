package jobs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class TimerJob implements Job{
	private Logger logger;
	protected JobDataMap jobParams;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		try {
			this.logger = Logger.getLogger(this.getClass());
			PatternLayout layout = new PatternLayout("%m%n");
			String fileName = "public/logs/" + this.getClass().getCanonicalName() + "_" + System.currentTimeMillis() + ".log";
			logger.addAppender(new FileAppender(layout, fileName));
			this.jobParams = jec.getJobDetail().getJobDataMap();
			long startTime = System.currentTimeMillis() / 1000;
			run();
			long totalTime = System.currentTimeMillis() / 1000 - startTime;
			int day = (int)TimeUnit.SECONDS.toDays(totalTime);        
			long hours = TimeUnit.SECONDS.toHours(totalTime) - (day *24);
			long minute = TimeUnit.SECONDS.toMinutes(totalTime) - (TimeUnit.SECONDS.toHours(totalTime)* 60);
			long second = TimeUnit.SECONDS.toSeconds(totalTime) - (TimeUnit.SECONDS.toMinutes(totalTime) *60);
			log("Job finished in: " + day + " day(s), " + hours + " hour(s), " + minute + " min(s), " + second + " second(s)");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			Enumeration appenderEnum = logger.getAllAppenders();
			while (appenderEnum.hasMoreElements()) {
				Object obj = appenderEnum.nextElement();
				if (obj instanceof FileAppender) {
					((FileAppender) obj).close();
				}
			}
		}
	}

	protected void log(String message) {
		logger.log(Priority.INFO, message);
	}

	protected abstract void run();
}
