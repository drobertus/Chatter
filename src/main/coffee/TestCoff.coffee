
addUserToList = (userName, listName) ->
  userList = document.getElementById(listName) #'userList');
  option = document.createElement("option")
  option.text = userName
  userList.add option
  return


showMsg = (sender, theMsg) ->
  console.log "sender=" + sender
  console.log "sender=" + message
  receivedTexts = document.getElementById("receivedChats")
  receivedTexts.value += "from " + sender + "->" + theMsg
  return

###
for use from the HTML interface- wraps the msg in the
appropriate JSON wrapper
###
sendChatMsg = (recipient, theMsg) ->
  send "{\"msgType\":\"sentMsg\", \"value\": {\"sentTo\": \"" + recipient + "\", \"msgBody\": \"" + theMsg + "\"}}"
  receivedTexts = document.getElementById("receivedChats")
  receivedTexts.value += "to " + recipient + "->" + theMsg
  return

send = (msg) ->
  ws.send msg
  return

ws = new WebSocket("ws://127.0.0.1:8555")

ws.onopen = ->
  send "{\"msg\": \"Hello Server\"}"
  return

ws.onmessage = (evt) ->
  msg = evt.data
  console.log "onMsg=" + msg
  obj = JSON.parse(msg)
  msgType = obj.msgType
  value = obj.value
  switch msgType
    when "userList"
      i = 0
      while i < value.length
        addUserToList value[i], "userList"
        i++
    when "userAdd"
      addUserToList value, "userList"
    when "userDelete"
      removeUserFromList value, "userList"
    when "receiveMsg"
      showMsg value.sender, value.message
    when "error"
      alert value
    else
      console.log "message type unrecognized"

ws.onclose = ->
  alert "Closed!"
  return

ws.onerror = (err) ->
  alert "Error: " + err
  return

String::contains = (it) ->
  @indexOf(it) isnt -1
