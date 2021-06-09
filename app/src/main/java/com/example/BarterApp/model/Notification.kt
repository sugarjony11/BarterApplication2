package com.example.BarterApp.model

class Notification {
    var productID: String? = null
    var productUserID : String? = null
    var RequestUserID : String? = null
    var OfferProductID1: String? = null
    var OfferProductID2: String? = null
    var OfferProductID3: String? = null
    var OfferID : String? = null
    var OfferStatus : String? = null
    var Timestamp : String? = null
    constructor() {

    }

    constructor(ProductID: String?,ProductUserID : String?, RequestUserID : String?,OfferID : String?,OfferStatus : String?,OfferProductID1: String?,OfferProductID2: String?,OfferProductID3: String?,Timestamp : String?) {
        this.productID = ProductID
        this.productUserID = ProductUserID
        this.RequestUserID = RequestUserID
        this.OfferID = OfferID
        this.OfferStatus = OfferStatus
        this.OfferProductID1 = OfferProductID1
        this.OfferProductID2 = OfferProductID2
        this.OfferProductID3 = OfferProductID3
        this.Timestamp = Timestamp
    }
}