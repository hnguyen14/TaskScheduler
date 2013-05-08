package jobs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public abstract class TimerJob implements Job{
	private Logger logger;
	protected JobDataMap jobParams;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		String fileName = this.getClass().getCanonicalName() + "_" + System.currentTimeMillis() + ".log";
		String fsPath = "public/logs/" + fileName;
		boolean logToFile = false;
		Throwable error = null;
		long startTime = System.currentTimeMillis() / 1000;

		/*
		 * Set up logger
		 */
		this.logger = Logger.getLogger(this.getClass());
		PatternLayout layout = new PatternLayout("%m%n");

		try {
			logger.addAppender(new FileAppender(layout, fsPath));
			logToFile = true;
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		/*
		 * Running test main task
		 */
		try {
			// Setting up to run the task
			this.jobParams = jec.getJobDetail().getJobDataMap();
			run();
		} catch (Exception e) {
			error = e;
			logError(e.getMessage(), e);
		}

		/*
		 * Task run data logging
		 */
		long totalTime = System.currentTimeMillis() / 1000 - startTime;
		int day = (int)TimeUnit.SECONDS.toDays(totalTime);        
		long hours = TimeUnit.SECONDS.toHours(totalTime) - (day *24);
		long minute = TimeUnit.SECONDS.toMinutes(totalTime) - (TimeUnit.SECONDS.toHours(totalTime)* 60);
		long second = TimeUnit.SECONDS.toSeconds(totalTime) - (TimeUnit.SECONDS.toMinutes(totalTime) *60);
		log("Job finished in: " + day + " day(s), " + hours + " hour(s), " + minute + " min(s), " + second + " second(s)");

		/*
		 * Sending email
		 */
		String email = jobParams.getString("email");
		if (email != null) {
			String publicPath = "/assets/logs/" + fileName;
			MailerPlugin plugin = play.Play.application().plugin(MailerPlugin.class);
			if (plugin != null) {
				MailerAPI mail = plugin.email();
				mail.setSubject((error != null ? "[ERROR]" : "") + "Task " + jec.getJobDetail().getKey().getName() + "finished");
				mail.addRecipient(email);
				mail.addFrom("TaskScheduler Admin <noreply@taskscheduler.com>");
				String body = "";
				if (error != null) {
					body = error.getClass().getName() + ":" + error.getMessage(); 
					for(StackTraceElement ste : error.getStackTrace()) {
						body += "\n\t" + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";
					}
				} else {
					body = "Finished successfully";
				}
				body += "\n";
				body += "Task log: " + (logToFile ? publicPath : "Not available due to configuration error") + "\n" +
						"Task finished in: " + day + " day(s), " + hours + " hour(s), " + minute + " min(s), " + second + " second(s)";
				mail.send(body);
				log("Email sent to " + email);
			} else {
				log("Email plugin not found. No email sent to " + email);
			}
		} else {
			log ("No email specified in task params. No email sent.");
		}

		/*
		 * Close all log file appender
		 */
		Enumeration appenderEnum = logger.getAllAppenders();
		while (appenderEnum.hasMoreElements()) {
			Object obj = appenderEnum.nextElement();
			if (obj instanceof FileAppender) {
				((FileAppender) obj).close();
			}
		}
	}

	protected void log(String message) {
		logger.log(Level.INFO, message);
	}

	protected void logError(String message, Throwable t) {
		logger.log(Level.INFO, message, t);
	}

	protected abstract void run();
}
