$ ->
  clearForm = ->
    $("#taskName").val ""
    $("#className").val ""
    $("#taskParam").val ""
    $("#startTime").val ""
    $("#interval").val ""

  $("#newTaskBtn, #newTask button.cancel").click (e) ->
    clearForm()
    $("#newTask").toggle 500

  $("#newTask form").submit ->
    taskName = $("#taskName").val()
    className = $("#className").val()
    taskParam = $("#taskParam").val()
    startTime = $("#startTime").val()
    interval = $("#interval").val()
    $.post "/tasks/create",
      taskName: taskName
      className: className
      taskParam: taskParam
      startTime: startTime
      interval: interval
    , (data) ->
      $("table.tasks").append data

    false
