package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class Task extends Controller{
	public static Result index() {
		return ok(taskIndex.render());
	}
	
	public static Result create() {
		return ok(taskCreate.render("taskName", "className", "taskParam", "startTime", "interval"));
	}
}
