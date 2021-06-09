package com.example.BarterApp.model

class Product {
    var ProductID: String? = null
    var USerID: String? = null
    var ProductName: String? = null
    var ProductType: String? = null
    var ProductState: String? = null
    var ProductDetail: String? = null
    var ProductImage: String? = null
    var ProductQuality: String? = null
    var ProductHand: Int? = null
    var ProductTime: String? = null
    var Score : Double? = null

constructor(){

}

    constructor(ProductID: String?, USerID: String?,ProductName: String?,ProductType: String?,ProductState: String?,ProductDetail: String?,ProductImage: String? ,ProductQuality: String?,ProductHand: Int?,ProductTime: String?,Score : Double?) {
        this.ProductID = ProductID
        this.USerID = USerID
        this.ProductName = ProductName
        this.ProductType = ProductType
        this.ProductState = ProductState
        this.ProductDetail = ProductDetail
        this.ProductImage = ProductImage
        this.ProductQuality = ProductQuality
        this.ProductHand = ProductHand
        this.ProductTime = ProductTime
        this.Score = Score
    }
}

