package com.mechzombie.chatter.client

import com.mechzombie.chatter.protocol.BaseMessage
import com.mechzombie.chatter.protocol.MessageType
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.StatusCode
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

@WebSocket(maxTextMessageSize = 65536)
class SimpleTestSocket {

    def name
    def received = []

    @SuppressWarnings("unused")
    private Session session;

    public SimpleTestSocket(name) {
        this.name = name
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {

    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        this.session = null;
    //    this.closeLatch.countDown();
    }

    def shutdown() {
        session.close(StatusCode.NORMAL, "I'm done")
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session
        try {
            Future<Void> fut;

            def initMsg = BaseMessage.createEmptyBase(MessageType.hello)
            initMsg.setValueParam('name', name)

            fut = sendMsg(initMsg.toString())
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    def sendMsg(String msg) {
        Future<Void> fut;
        fut = session.getRemote().sendStringByFuture(msg);
        return fut
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        System.out.printf("Got msg: %s%n", msg);

        def theMsg = BaseMessage.parseMsg(msg)
        received << theMsg
    }
}
