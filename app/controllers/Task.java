package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.TimerJob;

import org.quartz.SchedulerException;

import play.mvc.Controller;
import play.mvc.Result;
import utils.SchedulerUtils;
import views.html.tasks.index;
import views.html.tasks.item;

public class Task extends Controller{

	public static Result index() {
		List<TimerJob> jobs = new ArrayList<TimerJob>();
		try {
			jobs = SchedulerUtils.getAlljob();
		} catch (SchedulerException se) {
			se.printStackTrace();
		}

		return ok(index.render(jobs));
	}

	public static Result create()  {
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		String jobName = params.get("jobName")[0];
		String jobClass = params.get("jobClass")[0];
		String jobParams = params.get("jobParams")[0];
		String jobStartTime = params.get("jobStartTime")[0];
		String jobInterval = params.get("jobInterval")[0];
		TimerJob tj = new TimerJob(jobName, jobClass, jobParams, jobStartTime, jobInterval);
		try {
			SchedulerUtils.scheduleJob(tj);
		} catch (SchedulerException se) {
			System.out.println(se.getMessage());
		} catch (ClassNotFoundException cnfe) {
			System.out.println(cnfe.getMessage());
		} catch (ParseException pe) {
			System.out.println(pe.getMessage());
		}

		return ok(item.render(tj));
	}
}
