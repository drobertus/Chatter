package org.test.ws

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Singleton
public class RoomListingImpl implements RoomListing {

    private static  def rooms

    MessageGenerator msgGen = new MessageGenerator()

    private Map<String, Session> connectedUsers = new HashMap<String, Session>();

    def roomMapping = [:]


    @Override
    public void addSession(String id, Session session) throws IOException {
        println("adding session 1")
        sendMessageToUser(session, "room", msgGen.getListOfConnectedUsers(getConnectedIds(), null));
        connectedUsers.put(id, session);
        // send a message to all users that this user has joined
        println("adding session 2")
        connectedUsers.keySet().each { key ->
            Session sess = connectedUsers.get(key)
            if (session != sess) {
                try {

                    sendMessageToUser(sess, id, '{"msgType": "userAdd", "value": "' + id + '"}');

                }catch(WebSocketException wse) {
                    println wse
                    connectedUsers.remove(key)
                }
            }

        }

    }

    @Override
    public void removeSession(String id) {

        connectedUsers.remove(id);
        //for each user send a remove message
    }

    def sendMessageToUser(String to, String msg) throws Exception {
        def outSess = this.connectedUsers.get(to)
        if (outSess == null) {
            // send out a message to remove this user from the
            //remaining participants lists
            throw new Exception("No msg recipient found" )
        }
        sendMessageToUser(outSess, null, msg)
    }

    @Override
    public void sendMessageToUser(Session to, String from, String message)throws IOException {
        System.out.println("message to send is ${message}");
        RemoteEndpoint re = to.getRemote()
        re.sendString(message);
        println "sent msg"
    }

    @Override
    public Set<String> getConnectedIds() {
        Set<String> keyCopy = new HashSet<>();
        keyCopy.addAll(connectedUsers.keySet());
        return keyCopy;
    }

}
