package com.example.BarterApp.model

class Feedback {
    var sender: String? = null //mail ผู้ใช้
    var TradID: String? = null //id trading
    var reader: String? = null //mail คนอื่น(ที่แลกเปลี่ยน)
    var Rating: Int? = null
    var Comment: String? = null
var Image : String? = null
    constructor() {

    }

    constructor(sender: String?, TradID: String?, reader: String?, Rating: Int?, Comment: String?,Image : String? = null) {
        this.sender = sender
        this.TradID = TradID
        this.reader = reader
        this.Rating = Rating
        this.Comment = Comment
       this.Image = Image

    }


}
