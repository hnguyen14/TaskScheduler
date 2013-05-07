package controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Job;

import org.codehaus.jackson.node.ObjectNode;
import org.quartz.SchedulerException;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import utils.SchedulerUtils;
import views.html.tasks.index;
import views.html.tasks.item;

public class Task extends Controller{

	public static final String ERROR_CLASS_NOT_FOUND = "classNotFound";
	public static final String ERROR_NUMBER_FORMAT = "numberFormat";
	public static final String ERROR_DATE_PARSING = "dateParsing";
	public static final String ERROR_SCHEDULER = "scheduler";

	public static Result index() {
		List<Job> jobs = new ArrayList<Job>();
		try {
			jobs = SchedulerUtils.getAlljob();
		} catch (SchedulerException se) {
			se.printStackTrace();
		}

		return ok(index.render(jobs));
	}

	private static Job getTimerJobFromRequest() {
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		String jobName = params.get("jobName")[0];
		String jobClass = params.get("jobClass")[0];
		String jobParams = params.get("jobParams")[0];
		String jobStartTime = params.get("jobStartTime")[0];
		String jobInterval = params.get("jobInterval")[0];
		return new Job(jobName, jobClass, jobParams, jobStartTime, jobInterval);
	}


	public static Result create()  {
		String errorMessage = null;
		String errorCode = null;
		Job tj = getTimerJobFromRequest();
		try {
			SchedulerUtils.scheduleJob(tj);
		} catch (ClassNotFoundException cnfe) {
			errorMessage = "Class not found: " + cnfe.getMessage();
			errorCode = ERROR_CLASS_NOT_FOUND;
		} catch (NumberFormatException mfe) {
			errorMessage = "Number format error: " + mfe.getMessage();
			errorCode = ERROR_NUMBER_FORMAT;
		} catch (ParseException pe) {
			errorMessage = "Date format error: " + pe.getMessage();
			errorCode = ERROR_DATE_PARSING;
		} catch (SchedulerException se) {
			errorMessage = se.getMessage();
			errorCode = ERROR_SCHEDULER;
		}

		if (errorMessage == null) {
			return ok(item.render(tj));
		} else {
			ObjectNode result = Json.newObject();
			result.put("error_message", errorMessage);
			result.put("error_code", errorCode);
			return badRequest(result);
		}
	}

	public static Result delete() {
		String errorMessage = null;
		String errorCode = null;
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		String jobName = params.get("jobName")[0];
		try {
			SchedulerUtils.deleteJob(jobName);
		} catch (SchedulerException se) {
			errorMessage = se.getMessage();
			errorCode = ERROR_SCHEDULER;
		}
		if (errorMessage == null) {
			return ok();
		} else {
			ObjectNode result = Json.newObject();
			result.put("error_message", errorMessage);
			result.put("error_code", errorCode);
			return badRequest(result);
		}
	}
	
	public static Result pause() {
		String errorMessage = null;
		String errorCode = null;
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		String jobName = params.get("jobName")[0];
		try {
			SchedulerUtils.pauseJob(jobName);
		} catch (SchedulerException se) {
			errorMessage = se.getMessage();
			errorCode = ERROR_SCHEDULER;
		}
		if (errorMessage == null) {
			return ok();
		} else {
			ObjectNode result = Json.newObject();
			result.put("error_message", errorMessage);
			result.put("error_code", errorCode);
			return badRequest(result);
		}
	}
	
	public static Result resume() {
		String errorMessage = null;
		String errorCode = null;
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		String jobName = params.get("jobName")[0];
		try {
			SchedulerUtils.resumeJob(jobName);
		} catch (SchedulerException se) {
			errorMessage = se.getMessage();
			errorCode = ERROR_SCHEDULER;
		}
		if (errorMessage == null) {
			return ok();
		} else {
			ObjectNode result = Json.newObject();
			result.put("error_message", errorMessage);
			result.put("error_code", errorCode);
			return badRequest(result);
		}
	}

	public static Result update() {
		String errorMessage = null;
		String errorCode = null;
		Map<String, String[]> params = request().body().asFormUrlEncoded();
		Job tj = getTimerJobFromRequest();
		
		try {
			SchedulerUtils.deleteJob(params.get("jobOldName")[0]);
			SchedulerUtils.scheduleJob(tj);;
		} catch (ClassNotFoundException cnfe) {
			errorMessage = "Class not found: " + cnfe.getMessage();
			errorCode = ERROR_CLASS_NOT_FOUND;
		} catch (NumberFormatException mfe) {
			errorMessage = "Number format error: " + mfe.getMessage();
			errorCode = ERROR_NUMBER_FORMAT;
		} catch (ParseException pe) {
			errorMessage = "Date format error: " + pe.getMessage();
			errorCode = ERROR_DATE_PARSING;
		} catch (SchedulerException se) {
			errorMessage = se.getMessage();
			errorCode = ERROR_SCHEDULER;
		}

		if (errorMessage == null) {
			return ok(item.render(tj));
		} else {
			ObjectNode result = Json.newObject();
			result.put("error_message", errorMessage);
			result.put("error_code", errorCode);
			return badRequest(result);
		}
	}
}
