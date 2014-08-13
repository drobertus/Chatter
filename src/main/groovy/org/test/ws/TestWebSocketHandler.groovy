package org.test.ws

import groovy.json.JsonSlurper;
import org.eclipse.jetty.websocket.api.Session;
//import org.eclipse.jetty.websocket.common.WebSocketSession
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;

@WebSocket
public class TestWebSocketHandler {

    private static int ID_GEN = 0;

    private Session theSession;
    private String id;

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        RoomListingImpl.instance.removeSession(id);
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @OnWebSocketError
    public void onError(Throwable t) {
        System.out.println("Error on web socket: " + t.getMessage());
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        String address = session.getRemoteAddress().getAddress().getCanonicalHostName();
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        this.theSession = session;
        try {
            id = (ID_GEN ++) +"_" + address
            RoomListingImpl.instance.addSession(id, session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Message: " + message);
        def slurper = new JsonSlurper()
        def result = slurper.parseText(message)

        def msgType = result.msgType
        def value = result.value

        switch (msgType) {
            case "sentMsg":
                def sentTo = value.sentTo
                //def sentFrom = value.sentFrom
                def msgBody = value.msgBody

                //TODO: re-format the message here
                //get the name of the sender from the session data
                def relayedMsg = "{\"msgType\": \"receiveMsg\", \"value\": {\"sender\": \"${id}\", \"message\": \"${msgBody}\"}}"
                try {
                    RoomListingImpl.instance.sendMessageToUser(sentTo, relayedMsg)
                }catch(IOException ioe) {
                    //stuff
                }
                catch(Exception e) {
                    RoomListingImpl.instance.sendMessageToUser(this.theSession, "System",
                    '{"msgType": "error", "value": "' + e.getMessage() + '"}')
                }
                break;

            case "disconnect":
                break;
            default:

                break

        }

//        try {
//            theSession.getRemote().sendString("got: " + message);
//        }catch(IOException ioe) {
//            ioe.printStackTrace();
//
//        }
    }
}