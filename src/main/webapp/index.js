console.log("path=" + location.host + " + /chat");
var ws = new WebSocket("ws://" + location.host + "/chat");

ws.onopen = function() {
    //alert("Opened!");
    send('{"msgType": "hello"}');
};

ws.onmessage = function (evt) {
    var msg = evt.data;

    console.log('onMsg=' + msg);
    var obj = JSON.parse(msg);
    //TODO: define a structure for data exchange
    //var currentList = obj.users;
    var msgType = obj.msgType;
    var value = obj.value
    switch (msgType) {
     case 'userList':
        for (i = 0; i < value.length; i ++) {
            addUserToList (value[i], 'userList');
        }
        break;
     case 'userAdd' :
        addUserToList (value, 'userList');
        break;
     case 'userDelete' :
        removeUserFromList (value, 'userList');
        break;
     case 'hello':
        //we have connected= need to set a name
        console.log('greeting received from server')
        break;
     case 'userRegistered':
        //enable the UI components
        enableUIForChat(value.registeredName);
        break;
     case 'receiveMsg' :
        showMsg(value.sender, value.message);
        break;
     case 'error' :
        alert(value);
        break;
     default:
        console.log('message type unrecognized');
        break;

    }

};


function addUserToList(userName, listName) {
    var userList = document.getElementById(listName); //'userList');
    var option = document.createElement("option");
    option.text = userName;
    userList.add(option);
}

ws.onclose = function() {
    alert("Closed!");
};

ws.onerror = function(err) {
    alert("Error: " + err);
};


function showMsg(sender, theMsg) {
    console.log('sender=' + sender);
    console.log('message=' + theMsg);
    var receivedTexts = document.getElementById('receivedChats');
    receivedTexts.value += 'from ' + sender + '->' + theMsg + '\n';
}

function setMyName(myName) {
    send('{"msgType":"setUserName","value":{"name":"' + myName + '"}}');
}
/** for use from the HTML interface- wraps the msg in the
appropriate JSON wrapper
*/
function sendChatMsg(recipient, theMsg){

    send( "{\"msgType\":\"sentMsg\", \"value\": {\"sentTo\": \"" +
    recipient + "\", \"msgBody\": \"" + theMsg + "\"}}");

    var receivedTexts = document.getElementById('receivedChats');
    receivedTexts.value += 'to ' + recipient + '->' + theMsg + '\n';
}

//this turns on the main part of the UI for
function enableUIForChat(nameToUse) {
    document.getElementById('chatDiv').style.display = 'block';
    document.getElementById('userNameDiv').style.display = 'none';
}

function send(msg) {
    ws.send(msg);
}



String.prototype.contains = function(it) { return this.indexOf(it) != -1; };