package jobs;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Counter implements Job{

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		JobDataMap jdm = jec.getJobDetail().getJobDataMap();
		
		for (int i = 0; i < jdm.getInt("count"); i++) {
			System.out.println("COUNT " + i);
		}
	}
	

}
