$ ->
  clearForm = ->
    $("#jobName").val ""
    $("#jobClass").val ""
    $("#jobParams").val ""
    $("#jobStartTime").val ""
    $("#jobInterval").val ""

  $("#newTaskBtn, #newTask button.cancel").click (e) ->
    clearForm()
    $("#newTask").toggle 500

  $("#newTask form").submit ->
    jobName = $("#jobName").val()
    jobClass = $("#jobClass").val()
    jobParams = $("#jobParams").val()
    jobStartTime = $("#jobStartTime").val()
    jobInterval = $("#jobInterval").val()
    console.log jobName, jobClass, jobParams, jobStartTime, jobInterval
    $.post "/tasks/create",
      jobName: jobName
      jobClass: jobClass
      jobParams: jobParams
      jobStartTime: jobStartTime
      jobInterval: jobInterval
    , (data) ->
      $("table.tasks").append data

    false
