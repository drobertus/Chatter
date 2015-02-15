package com.mechzombie.chatter.client

import com.mechzombie.chatter.ChatterWSServlet
import com.mechzombie.chatter.protocol.MessageType
import com.sun.xml.internal.ws.transport.http.server.ServerContainer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.websocket.server.WebSocketHandler
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import com.mechzombie.chatter.ChatterSocketListener
import spock.lang.Specification

import static groovy.util.GroovyTestCase.assertEquals

class ChatServerIntegTest extends Specification {

    Server server
    def wsClient1
    def wsClient2
    def serverPort = 8559


    def "test client to client chat"() {

        setup: " we define the address for the clients to access"

            def address = "ws://127.0.0.1:${serverPort}"
        when: " we connect two clients that say hello with distinct names"
            wsClient1 = new TestWSClient(address, 'Bob')
            wsClient2 = new TestWSClient(address, 'Sally')
            // TODO: make listenable future for client state, a "ready" or
            // "initialized" command
            sleep(3500)

        then: "each should receive a pair of messages after they say hello"
            //def received = wsClient2.getReceivedMsgs()
            def msgs2 = wsClient2.getReceivedMsgs();
            def msgs1 = wsClient1.getReceivedMsgs();

            assertEquals 2, msgs2.size()
            assertEquals MessageType.userList, msgs2[0].msgType
            assertEquals MessageType.userAdd, msgs2[1].msgType

            assertEquals 2, wsClient1.getReceivedMsgs().size()
            assertEquals MessageType.userList, msgs1[0].msgType
            assertEquals MessageType.userAdd, msgs1[1].msgType

        when: "one client sends a chat message to the other"
            wsClient2.receivedMsgs.clear()
            wsClient1.receivedMsgs.clear()
            def msgToSend = 'this is a chat'
            wsClient1.sendChatMsg('Sally', msgToSend)

            sleep(500)
        then: "the other client should receive that message identified as from the sender"
            def received = wsClient2.getReceivedMsgs()
            assertEquals 1, received.size()
            assertEquals MessageType.receiveMsg, received[0].msgType
            assertEquals msgToSend, received[0].getMsgParam('message')
            assertEquals wsClient1.name, received[0].getMsgParam('sender')
    }

    void setup() {
        server = new Server(serverPort);
        def context = new WebAppContext()
        context.setResourceBase("./src/main/webapp")
        server.setHandler(context)
        server.start();
        server.join()
    }

    void cleanup() {
        server.stop()
    }
}