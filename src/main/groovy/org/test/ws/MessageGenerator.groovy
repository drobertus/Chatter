package org.test.ws;


public class MessageGenerator {

    String getListOfConnectedUsers(Collection<String> userList, String userToReceive) {
        def json = new StringBuilder();
        json.append('{"msgType": "userList", "value": [')
        def isFirst = true;
        userList.each() { userName ->
            if (userToReceive != userName) {
                if (!isFirst) {
                    json.append(', ')
                }
                json.append('"' + userName + '"')
                isFirst = false
            }
        }

        json.append("]}")

        return json.toString()
    }

}
