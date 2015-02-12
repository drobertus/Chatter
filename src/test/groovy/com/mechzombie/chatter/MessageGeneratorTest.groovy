package com.mechzombie.chatter

import groovy.json.JsonSlurper
import org.junit.Assert
import spock.lang.Specification

/**
 * Created by David on 7/21/2014.
 */
class MessageGeneratorTest extends Specification {

    def msgGenerator //= new MessageGenerator()
    def userList //= ['Adam', 'Sandra', 'Zach', 'Barry']
    def activeUser

    void setup() {
        msgGenerator = new MessageGenerator()
        userList = ['Adam', 'Sandra', 'Zach', 'Barry']
    }

    void "test that the list of connected users should be in json format"() {
        when:
            activeUser = 'Thomas'
            def response = msgGenerator.getListOfConnectedUsers(userList, activeUser)

        then:
            assert !userList.contains(activeUser)
            println "response = ${response}"
            def slurper = new JsonSlurper()
            def json = slurper.parseText(response)
            //'[msgType:userList, value:[Adam, Sandra, Zach, Barry]]
            Assert.assertEquals json.msgType, 'userList'

            assert json.value == userList
    }

    void "test that the name of the requesting user is excluded from the return list"() {
        when:
            activeUser = userList.get(0)
            def response = msgGenerator.getListOfConnectedUsers(userList, activeUser)

        then:
            assert userList.contains(activeUser)
            println "response = ${response}"
            def slurper = new JsonSlurper()
            def json = slurper.parseText(response)

            assert json.value.size() == userList.size() -1
            assert !json.value.contains(activeUser)
    }
}
