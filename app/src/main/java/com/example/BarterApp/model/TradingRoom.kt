package com.example.BarterApp.model

class TradingRoom {
    var TradingID : String? = null
    var ProductUserID : String? = null
    var RequestUserID : String? = null
    var TradingStatus : String? = null

    var PD_Item1 : String? = null
    var PD_Item2 : String? = null
    var PD_Item3 : String? = null
    var PD_Item4 : String? = null
    var PD_Item5 : String? = null

    var RQ_Item1 : String? = null
    var RQ_Item2 : String? = null
    var RQ_Item3 : String? = null
    var RQ_Item4 : String? = null
    var RQ_Item5 : String? = null

    var PD_Status : String? = null
    var RQ_Status : String? = null
    constructor() {

    }

    constructor(TradingID : String?,ProductUserID : String?,RequestUserID : String?,TradingStatus : String?,
                PD_Item1 : String?,
                PD_Item2 : String? ,
                PD_Item3 : String?,
                PD_Item4 : String? ,
                PD_Item5 : String?,

                RQ_Item1 : String? ,
                RQ_Item2 : String?,
                RQ_Item3 : String?,
                RQ_Item4 : String?,
                RQ_Item5 : String?,

                PD_Status : String? ,
                RQ_Status : String?
    ) {
        this.TradingID = TradingID
        this.ProductUserID = ProductUserID
        this.RequestUserID = RequestUserID
        this.TradingStatus = TradingStatus

        this.PD_Item1 = PD_Item1
        this.PD_Item2 = PD_Item2
        this.PD_Item3 = PD_Item3
        this.PD_Item4 = PD_Item4
        this.PD_Item5 = PD_Item5

        this.RQ_Item1 = RQ_Item1
        this.RQ_Item2 = RQ_Item2
        this.RQ_Item3 = RQ_Item3
        this.RQ_Item4 = RQ_Item4
        this.RQ_Item5 = RQ_Item5

        this.PD_Status = PD_Status
        this.RQ_Status = RQ_Status

    }
}