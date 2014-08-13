package org.test.ws

import org.eclipse.jetty.websocket.api.RemoteEndpoint
import org.eclipse.jetty.websocket.api.Session
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by David on 8/2/2014.
 */
class RoomListingSpec extends Specification {

    def roomListing = RoomListingImpl.instance
    def user1Name = "Bob"
    def bobSession  = Mock(Session)
    def remoteEndpoint = Mock(RemoteEndpoint)

    def user2Name = "Sally"
    def sallySession  = Mock(Session)
    def remoteEndpointSally = Mock(RemoteEndpoint)

    void "When the first user arrives the user should receive an empty list of room users"() {
        setup:
            bobSession.getRemote() >> remoteEndpoint
            sallySession.getRemote() >> remoteEndpointSally

        when: "the first user arrives"
            roomListing.addSession(user1Name, bobSession)
        then: "He gets an empty list of room participants and an ACK"

            1 * remoteEndpoint.sendString('{"msgType": "userList", "value": []}')
            0 * remoteEndpoint._

        when: "the next participant arrives"
            roomListing.addSession(user2Name, sallySession)

        then: "he gets the first entrant as a participant list and an ACK"
            assert 2 == roomListing.connectedIds.size()
            //println roomListing.connectedIds

            1 * remoteEndpointSally.sendString('{"msgType": "userList", "value": ["Bob"]}')

        and: "the first entrant receives the second user as a added user"
            1 * remoteEndpoint.sendString('{"msgType": "userAdd", "value": "Sally"}')

            0 * remoteEndpoint._
            0 * remoteEndpointSally._

        when: "the first user sends a chat message"


        then: "the first should receive it"

        when: "the first user leaves the room"

        then: "the second should receive the notice and the participant count should be adjusted"



    }

    void "No user should see himself in the user listing"() {

    }

    void "When a user leaves the room all remaining users should receive the notification"() {

    }

    void "when the last user leaves a room the room should close after the specified wait"() {

    }
}
