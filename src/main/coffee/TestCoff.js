// Generated by CoffeeScript 1.7.1
(function() {
  var Chat;

  Chat = (function() {
    function Chat(ws) {
      this.ws = ws;
    }

    return Chat;

  })();

  ({
    addUserToList: function(userName, listName) {
      var option, userList;
      userList = document.getElementById(listName);
      option = document.createElement("option");
      option.text = userName;
      userList.add(option);
    },
    showMsg: function(sender, theMsg) {
      var receivedTexts;
      console.log("sender=" + sender);
      console.log("sender=" + message);
      receivedTexts = document.getElementById("receivedChats");
      receivedTexts.value += "from " + sender + "->" + theMsg;
    },

    /*
    for use from the HTML interface- wraps the msg in the
    appropriate JSON wrapper
     */
    sendChatMsg: function(recipient, theMsg) {
      var receivedTexts;
      send("{\"msgType\":\"sentMsg\", \"value\": {\"sentTo\": \"" + recipient + "\", \"msgBody\": \"" + theMsg + "\"}}");
      receivedTexts = document.getElementById("receivedChats");
      receivedTexts.value += "to " + recipient + "->" + theMsg;
    },
    send: function(msg) {
      ws.send(msg);
    }
  });

  ws.onopen = function() {
    send("{\"msg\": \"Hello Server\"}");
  };

  ws.onmessage = function(evt) {
    var i, msg, msgType, obj, value, _results;
    msg = evt.data;
    console.log("onMsg=" + msg);
    obj = JSON.parse(msg);
    msgType = obj.msgType;
    value = obj.value;
    switch (msgType) {
      case "userList":
        i = 0;
        _results = [];
        while (i < value.length) {
          addUserToList(value[i], "userList");
          _results.push(i++);
        }
        return _results;
        break;
      case "userAdd":
        return addUserToList(value, "userList");
      case "userDelete":
        return removeUserFromList(value, "userList");
      case "receiveMsg":
        return showMsg(value.sender, value.message);
      case "error":
        return alert(value);
      default:
        return console.log("message type unrecognized");
    }
  };

  ws.onclose = function() {
    alert("Closed!");
  };

  ws.onerror = function(err) {
    alert("Error: " + err);
  };

  String.prototype.contains = function(it) {
    return this.indexOf(it) !== -1;
  };

}).call(this);
