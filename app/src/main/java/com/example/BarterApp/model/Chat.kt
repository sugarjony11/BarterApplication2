package com.example.BarterApp.model

class Chat {
    var sender: String? = null
    var senderName : String? = null
    var senderPic : String? = null
    var reader: String? = null
    var message: String? = null
    var timestamp : String? = null

    constructor() {

    }

    constructor(sender: String?, senderName: String?, senderPic: String?, reader: String?, message: String?, timestamp: String?) {
        this.sender = sender
        this.senderName = senderName
        this.senderPic = senderPic
        this.reader = reader
        this.message = message
        this.timestamp = timestamp
    }


}