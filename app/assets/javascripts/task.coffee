clearForm = (form, clearVal) -> 	
  if (clearVal)
    form.find('[name="jobName"]').val ""
    form.find('[name="jobClass"]').val ""
    form.find('[name="jobParams"]').val ""
    form.find('[name="jobStartTime"]').val ""
    form.find('[name="jobInterval"]').val ""
  form.prev().hide()
  form.parents('.form-container').toggle 500

clearError = (form) ->
  form.find('.control-group').removeClass 'error'

getJobFormData = (form) ->
  retval =
    jobName: form.find('[name="jobName"]').val()
    jobOldName: form.find('[name="jobOldName"]').val()
    jobClass: form.find('[name="jobClass"]').val()
    jobParams: form.find('[name="jobParams"]').val()
    jobStartTime: form.find('[name="jobStartTime"]').val()
    jobInterval: form.find('[name="jobInterval"]').val()

handleError = (form, error) ->
  form.prev().text(error.error_message).show 500
  if (error.error_code == 'classNotFound')
    form.find('[name="jobClass"]').parents('.control-group').addClass('error')
  else if (error.error_code == 'dateParsing')
    form.find('[name="jobStartTime"]').parents('.control-group').addClass('error')
  else if (error.error_code == 'numberFormat')
    form.find('[name="jobInterval"]').parents('.control-group').addClass('error')
 
$ ->
  $("#newTaskBtn, #newTask button.cancel").click ->
  	clearForm $("#newTask form"), true

  $("#newTask form").submit ->
    form  = $(@)
    clearError form
    $.post("/tasks/create", getJobFormData(form), (data) ->
      $("table.tasks").append data
    ).done( ->
      clearForm form, true
    ).fail((body) ->
      handleError form, JSON.parse(body.responseText)
    )
    false

  $(document).on
  	submit: ->
      form = $(@)
      clearError form
      $.post("/tasks/update", getJobFormData(form), (data) ->
        form.parents('tr').remove()
        $("table.tasks").append data
      ).done( ->
        clearForm form, true
      ).fail((body) ->
        handleError form, JSON.parse(body.responseText)
      )
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
  		$.post "/tasks/delete", 
  			jobName: $target.parents('a').attr('data-job-key')
  		, (data) ->
  			$target.parents('tr').remove();
  , '.job-delete'
	
