package com.mechzombie.chatter.client

import com.mechzombie.chatter.protocol.BaseMessage
import com.mechzombie.chatter.protocol.MessageType
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest
import org.eclipse.jetty.websocket.client.WebSocketClient

/**
 * Created by David on 1/17/2015.
 */
class TestWSClient {

    SimpleTestSocket socket
    String name
    def client

    TestWSClient(String address, String name) {

        this.name = name
        //String destUri = address
        client = new WebSocketClient();
        socket = new SimpleTestSocket(name);
        try {
            client.start();
            URI echoUri = new URI(address);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, echoUri, request);
            System.out.printf("Connecting to : %s%n", echoUri);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    def sendChatMsg(String recipient, String msg) {
        def sendMsg = BaseMessage.createEmptyBase(MessageType.sentMsg)
        sendMsg.setValueParam('msgBody', msg)
        sendMsg.setValueParam('sentTo', recipient)
        socket.sendMsg(sendMsg.toString())
    }

    def getReceivedMsgs() {
        socket.received
    }
}
