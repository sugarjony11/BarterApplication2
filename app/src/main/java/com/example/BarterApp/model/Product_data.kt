package com.example.BarterApp.model

class Product_data {
    var image: String? = null
    var productName: String? = null
    var productType: String? = null
    var productPercent: String? = null
    var productTime: String? = null

    constructor(){

    }

    constructor(image: String?,productName: String?,
                productType: String?,
                productPercent: String?,
                productTime: String?) {
        this.image = image
        this.productName = productName
        this.productType = productType
        this.productPercent = productPercent
        this.productTime = productTime
    }



 /*   var productImage: String? = null
    var productName: String? = null
    var productType: String? = null
    var productPercent: String? = null
    var productTime: String? = null


    constructor(){

    }

    constructor(
        productImage: String?,
        productName: String?,
        productType: String?,
        productPercent: String?,
        productTime: String?
    ) {
        this.productImage = productImage
        this.productName = productName
        this.productType = productType
        this.productPercent = productPercent
        this.productTime = productTime
    }

  */


}