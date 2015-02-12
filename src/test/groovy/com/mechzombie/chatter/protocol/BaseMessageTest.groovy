package com.mechzombie.chatter.protocol

import spock.lang.Specification

import static groovy.util.GroovyTestCase.assertEquals
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue


class BaseMessageTest extends Specification {


    def "test singleValue object marshall/un-marshall"() {
        setup:
            def sampleMsg = '{"msgType": "userAdd", "value": "Thomas"}'
        when:
            def obj = BaseMessage.parseMsg(sampleMsg)
            def theVal = obj.msgVal
        then:
            assertEquals MessageType.userAdd, obj.msgType
            assertTrue (theVal instanceof String)
            assertEquals theVal, 'Thomas'

        when:
            def asString = BaseMessage.serializeMessage(obj)
        then:
            assertEquals sampleMsg.toString(), asString
    }

    def "test listValue object marshall/un-marshall"() {
        setup:
            def sampleMsg = '{"msgType": "userList", "value": ["Bill", "Susan"]}'
        when:
            def obj = BaseMessage.parseMsg(sampleMsg)
            def theVal = obj.msgVal
        then:
            assertEquals MessageType.userList, obj.msgType

            assertTrue (theVal instanceof List)
            assertEquals 2, theVal.size()
            assertTrue theVal.contains('Bill')
            assertTrue theVal.contains('Susan')
        when:
            def asString = BaseMessage.serializeMessage(obj)
        then:
            assertEquals sampleMsg.toString(), asString

    }

    def "test mapValue object marshall/un-marshall"() {

        setup:
            def id = 'Bill'
            def msgBody = 'What hath God wrought?'
            def relayedMsg = "{\"msgType\": \"receiveMsg\", \"value\": {\"message\":\"${msgBody}\",\"sender\":\"${id}\"}}"

        when:
            //first marshall the object
            def theObj = BaseMessage.parseMsg(relayedMsg)
            def theVals = theObj.msgVal
            def theParams = theObj.params
            //println theParams
        then:
            assertTrue (theVals instanceof Map)
            assertEquals(MessageType.receiveMsg, theObj.msgType)
            assertTrue theParams.contains('sender')
            assertTrue theObj.params.contains('message')
            assertEquals id, theObj.getMsgParam('sender')
            assertEquals msgBody, theObj.getMsgParam('message')

        when:
            def serialized = BaseMessage.serializeMessage(theObj)
        then:
            assertEquals relayedMsg.toString(), serialized
    }

    void setup() {


    }

}