package com.mechzombie.chatter

import com.mechzombie.chatter.protocol.BaseMessage
import com.mechzombie.chatter.protocol.MessageType
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException

import java.util.concurrent.ConcurrentHashMap

@Singleton
public class RoomListingImpl implements RoomListing {

    private static  def rooms
    private Map<String, Session> connectedUsers = new ConcurrentHashMap<String, Session>();
    private Map<String, Session> registeredUsers = new ConcurrentHashMap<String, Session>()


    @Override
    public void addSession(String id, Session session) throws IOException {
        println("adding session " + id)
        connectedUsers.put(id, session);
        //this user is connected, but until more data is known about the user
        //we do not inform others of their presence or vice-versa
    }

    /**
     * remove the user from the map for connected users
     * and add it to the map for registered users
     * @param id
     * @param session
     * @param theHello
     */
    @Override
    void registerUser(final String id, final Session session, String userName) {
        // TODO: this implementation will not scale well- we need to
        // get some of this processing onto a separate thread

        // String userName = theHello.getMsgParam('name')
        // we need a case for an existing user
        if (registeredUsers.containsKey(userName)) {
            // TODO: send back an error message
            return;
        }

        if(connectedUsers.containsKey(id)) {
            println "adding user ${userName} for session ${id}"
            registeredUsers.put(userName.toString(), session)
            connectedUsers.remove(id.toString())
        }

        //the user is now registered
        def registered = BaseMessage.createEmptyBase(MessageType.userRegistered)
        registered.setValueParam('registeredName', userName)
        sendMessageToUser(session, "room", registered)


        def userList = BaseMessage.createEmptyBase(MessageType.userList)
        userList.msgVal = getRegisteredUsers(userName)

        sendMessageToUser(session, "room", userList)

        def addUserMsg = BaseMessage.createEmptyBase(MessageType.userAdd)
        addUserMsg.msgVal = userName
        registeredUsers.each {pair ->
            Session sess = pair.value
            if (session != sess) {
                try {
                    sendMessageToUser(sess, userName, addUserMsg)
                }catch(WebSocketException wse) {
                    println wse
                    // TODO: should this be on an iterator?
                    registeredUsers.remove(pair.key)
                }
            }
        }
    }

    @Override
    public void removeSession(String userName) {

        registeredUsers.remove(userName);

        def dropUserMsg = BaseMessage.createEmptyBase(MessageType.userDrop)
        dropUserMsg.msgVal = userName
        registeredUsers.keySet().each { key ->
            if (!key.equals(userName)) {
                Session sess = registeredUsers.get(key)
                try {
                    sendMessageToUser(sess, userName, dropUserMsg) //'{"msgType": "userAdd", "value": "' + id + '"}');
                }catch(WebSocketException wse) {
                    println wse
                    registeredUsers.remove(key)
                }
            }
        }
        //for each user send a remove message
    }

    @Override
    void sendMessageToUser(String to, BaseMessage msg) throws Exception {
        def outSess = this.registeredUsers.get(to)
        if (outSess == null) {
            // send out a message to remove this user from the
            //remaining participants lists
            println "reg user size= ${registeredUsers.size()}"
            println "${registeredUsers.toString()}"
            throw new Exception("No msg recipient found for ${to}" )
        }
        sendMessageToUser(outSess, 'system', msg)
    }

    @Override
    public void sendMessageToUser(Session to, String from, BaseMessage message)throws IOException {
        System.out.println("message to send is ${message}");
        RemoteEndpoint re = to.getRemote()
        re.sendString(message.toString());
        println "sent msg"
    }

    @Override
    public Set<String> getConnectedIds() {
        Set<String> keyCopy = new HashSet<>();
        keyCopy.addAll(connectedUsers.keySet());
        return keyCopy;
    }

    @Override
    public Set<String> getRegisteredUsers(String userName) {
        Set<String> keyCopy = new HashSet<>();
        keyCopy.addAll(registeredUsers.keySet());
        keyCopy.remove(userName)
        return keyCopy;
    }
}
