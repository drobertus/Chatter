package com.mechzombie.chatter.protocol

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class BaseMessage {

    final MessageType msgType
    def msgVal

    BaseMessage(MessageType type) {
        msgType = type;
        if (type.listOfValues) {
            msgVal = []
        }
        else if(type.params) {
            msgVal = [:]
            type.params.each {
                msgVal.put(it, null)
            }
        }
        else {
            msgVal = ''
        }
    }

    static BaseMessage createEmptyBase(MessageType type) {
        def bm = new BaseMessage(type)
    }

    static BaseMessage parseMsg(String message) {

        def slurper = new JsonSlurper()
        def result = slurper.parseText(message)
        def msgType = MessageType.valueOf(result.msgType)
        BaseMessage msg = new BaseMessage(msgType)
        //TODO: how will this behave with String vs list vs. map?
        msg.msgVal = result.value
        return msg
    }

    static String serializeMessage(BaseMessage msg) {
        def writer = new StringWriter()
        writer.append('{')
        writer.append(paramToString('msgType', msg.msgType.toString()))
        writer.append ', "value": '

        if(msg.msgType.listOfValues) {
            writer.append'['
            def isFirst = true
            msg.msgVal.each() {
                if(!isFirst) {
                    writer.append(', ')
                }
                writer.append(paramToString(it))
                isFirst = false
            }
            writer.append']'
        }
        else {
            if (!msg.params) {
                writer.append(paramToString(msg.msgVal))
            }
            else {
                writer.append(JsonOutput.toJson(msg.msgVal))
            }
        }
        writer.append('}')
        return writer.toString()
    }

    private static String paramToString(String val) {
        return "\"${val}\""
    }

    private static String paramToString(String key, String val) {
        return "\"${key}\": \"${val}\""
    }

    def getMsgValueArray(int valPos) {
        if( msgType.listOfValue) {
            return msgVal[valPos]
        }
        return msgVal
    }

    def getParams() {
        return msgType.params
    }

    def getMsgParam (String paramName) {
        return msgVal[paramName]
    }

    @Override
    String toString() {
        BaseMessage.serializeMessage(this)
    }
    void setValueParam(String paramName, String value) {
        if(this.msgType.params && msgType.params.contains(paramName)) {
            msgVal.put(paramName, value)
        }
        else {
            throw new NoSuchFieldException("The property '${paramName}' not found for message type ${msgType.toString()}")
        }
    }
}
