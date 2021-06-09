package com.example.BarterApp.model

class Favorite {
    var UserID: String? = null //mail ผู้ใช้
    var UID: String? = null //id ผู้ใช้
    var ProductID: String? = null //รหัสสินค้า
    var ProductImage: String? = null
    var ProductName: String? = null
    var ProductHand: String? = null
    var ProductQuality: String? = null
    var ProductTime: String? = null
    var ProductDetail: String? = null
    var ProductState: String? = null
    var Fav: String? = null


    constructor() {

    }

    constructor(UserID: String?, UID: String?, ProductID: String?, ProductImage: String?, ProductName: String?, ProductHand: String?, ProductQuality: String?, ProductTime: String?, ProductDetail: String?, ProductState: String?,Fav: String?) {
        this.UserID = UserID
        this.UID = UID
        this.ProductID = ProductID
        this.ProductImage = ProductImage
        this.ProductName = ProductName
        this.ProductHand = ProductHand
        this.ProductQuality = ProductQuality
        this.ProductTime = ProductTime
        this.ProductDetail = ProductDetail
        this.ProductState = ProductState
        this.Fav = Fav
    }


}