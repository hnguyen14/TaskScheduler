clearForm = (form, clearVal) -> 	
  if (clearVal)
    form.find('[name="jobName"]').val ""
    form.find('[name="jobClass"]').val ""
    form.find('[name="jobParams"]').val ""
    form.find('[name="jobStartTime"]').val ""
    form.find('[name="jobInterval"]').val ""
  form.parents('.form-container').toggle 500

getJobFormData = (form) ->
  retval =
    jobName: form.find('[name="jobName"]').val()
    jobClass: form.find('[name="jobClass"]').val()
    jobParams: form.find('[name="jobParams"]').val()
    jobStartTime: form.find('[name="jobStartTime"]').val()
    jobInterval: form.find('[name="jobInterval"]').val()
$ ->
  $("#newTaskBtn, #newTask button.cancel").click ->
  	clearForm $("#newTask form"), true

  $("#newTask form").submit ->
    form  = $(@)
    $.post "/tasks/create", getJobFormData(form), (data) ->
      $("table.tasks").append data
      clearForm form, true
    false

  $(document).on
  	submit: ->
      form = $(@)
      data = getJobFormData(form)
      console.log data
      $.post "/tasks/update", getJobFormData(form), (data) ->
        form.parents('tr').remove()
        $("table.tasks").append data
        clearForm form, true
      false
  , '.job-modify form'

  $(document).on
  	click: (e) ->
  		e.preventDefault()
  		clearForm $(@).parents('form'), false
  , '.job-modify button.cancel'

  $(document).on
  	click: (e) ->
  		$target = $(e.target)
  		$target.parents('tr').find('.job-modify').toggle 500
  , '.job-data'

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
	
