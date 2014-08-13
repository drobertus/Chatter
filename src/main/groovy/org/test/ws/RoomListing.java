package org.test.ws;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;

/**
 * Created by David on 7/20/2014.
 */
public interface RoomListing {

    public void addSession(String id, Session session) throws IOException;
    public void removeSession(String id);

    public Set<String> getConnectedIds();
    public void sendMessageToUser(Session to, String from, String message)throws IOException;
}
