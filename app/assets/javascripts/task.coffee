$ ->
  clearForm = (form, clearVal) ->
  	if (clearVal)
    	form.find('[name="jobName"]').val ""
    	form.find('[name="jobClass"]').val ""
    	form.find('[name="jobParams"]').val ""
    	form.find('[name="jobStartTime"]').val ""
    	form.find('[name="jobInterval"]').val ""
    form.parent().toggle 500

  $("#newTaskBtn, #newTask button.cancel").click ->
  	clearForm $("#newTask form"), true

  $("#newTask form").submit ->
    form  = $(@)
    jobName = form.find('[name="jobName"]').val()
    jobClass = form.find('[name="jobClass"]').val()
    jobParams = form.find('[name="jobParams"]').val()
    jobStartTime = form.find('[name="jobStartTime"]').val()
    jobInterval = form.find('[name="jobInterval"]').val()
    console.log jobName, jobClass, jobParams, jobStartTime, jobInterval
    $.post "/tasks/create",
      jobName: jobName
      jobClass: jobClass
      jobParams: jobParams
      jobStartTime: jobStartTime
      jobInterval: jobInterval
    , (data) ->
      $("table.tasks").append data
      clearForm form, true
    false

  $(document).on
  	click: (e) ->
  		$target = $(e.target)
  		$target.parents('tr').next().toggle 500
  , '.job-row'

  $(document).on
  	click: (e) ->
  		e.stopPropagation()
  		$target = $(e.target)
  		console.log $target.parents('a').attr('data-job-key')
  		$.post "/tasks/delete", 
  			jobName: $target.parents('a').attr('data-job-key')
  		, (data) ->
  			$target.parents('tr').remove();
  , '.job-delete'
	
