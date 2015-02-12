package com.mechzombie.chatter

import com.mechzombie.chatter.protocol.BaseMessage
import com.mechzombie.chatter.protocol.MessageType
import groovy.util.logging.Log;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener

import java.util.concurrent.atomic.AtomicInteger;

@Log
public class ChatterSocketListener implements WebSocketListener {

    private static def ID_GEN = new AtomicInteger(0);
    private Session session;
    private RoomListing rl = RoomListingImpl.getInstance();
    private String id;
    private String userName;

    @Override
    public void onWebSocketBinary(byte[] bytes, int i, int i1) {

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        rl.removeSession(userName, id);
        System.out.println("Close: statusCode=" + statusCode + ", reason=" + reason);
    }

    @Override
    public void onWebSocketConnect(Session session) {

        String address = session.getRemoteAddress().getAddress().getCanonicalHostName();
        System.out.println("Connect: " + session.getRemoteAddress().getAddress());
        this.session = session;
        try {
            id = "${ID_GEN.incrementAndGet()}_${address}"
            rl.addSession(id, session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebSocketError(Throwable throwable) {

    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("Message: " + message);

        def theMsg = BaseMessage.parseMsg(message)
        switch (theMsg.msgType) {
            case MessageType.sentMsg: // "sentMsg":
                def sentTo = theMsg.getMsgParam('sentTo')// value.sentTo
                println("sending msg  to ${sentTo}")
                def msgBody = theMsg.getMsgParam('msgBody')

                //TODO: re-format the message here
                //get the name of the sender from the session data
                def relayedMsg = BaseMessage.createEmptyBase(MessageType.receiveMsg)
                relayedMsg.setValueParam('sender', userName)
                relayedMsg.setValueParam('message', msgBody)
                //"{\"msgType\": \"receiveMsg\", \"value\": {\"sender\": \"${id}\", \"message\": \"${msgBody}\"}}"
                try {
                    rl.sendMessageToUser(sentTo, relayedMsg)
                } catch (IOException ioe) {
                    println ioe
                }
                catch (Exception e) {
                    def errMsg = BaseMessage.createEmptyBase(MessageType.error)
                    errMsg.msgVal = e.getMessage()
                    rl.sendMessageToUser(this.session, "System", errMsg)
                    //'{"msgType": "error", "value": "' + e.getMessage() + '"}')
                }
                break;

            case MessageType.hello:
                def iSeeYou = BaseMessage.createEmptyBase(MessageType.hello)
                rl.sendMessageToUser(this.session, "System", iSeeYou)
                break;

            case MessageType.setUserName:
                userName = theMsg.getMsgParam('name')
                rl.registerUser(id, this.session, userName)
                break;
            case "disconnect":
                break;
            default:

                break

        }
    }
}
