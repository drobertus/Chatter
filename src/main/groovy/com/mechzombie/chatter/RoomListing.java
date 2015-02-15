package com.mechzombie.chatter;

import com.mechzombie.chatter.protocol.BaseMessage;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;

/**
 * This is the basic interface to conform to
 * for the implementation
 */
public interface RoomListing {

    public void addSession(String id, Session session) throws IOException;
    public void registerUser(String id, Session session, String userName);

    public void removeSession(String id);

    public Set<String> getConnectedIds();

    public Set<String> getRegisteredUsers(String userToReturnTo);

    void sendMessageToUser(String to, BaseMessage msg) throws Exception;

    void sendMessageToUser(Session to, String from, BaseMessage message)throws IOException;
}
