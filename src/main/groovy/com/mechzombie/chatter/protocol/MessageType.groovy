package com.mechzombie.chatter.protocol


enum MessageType {

    //TODO: may need 3+ type of values- String, list, likely map as well.
    userList(true),
    userAdd(false),
    userDrop(false),
    receiveMsg(false, ['sender', 'message']),
    sentMsg(false, ['sentTo', 'msgBody']),
    hello(false),
    setUserName(false, ['name']),
    error(false),
    userRegistered(false, ['registeredName'])



    def listOfValues
    def params= []
    MessageType(boolean valList, List<String> params = null) {
        this.listOfValues = valList
        this.params = params
    }

    def valueIsList() {
        return listOfValues
    }


}