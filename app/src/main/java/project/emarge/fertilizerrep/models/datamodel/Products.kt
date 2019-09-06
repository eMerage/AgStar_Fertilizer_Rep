package project.emarge.fertilizerrep.models.datamodel

import android.net.Uri
import com.google.gson.annotations.SerializedName
import project.emarge.fertilizerrep.model.datamodel.NetworkError


data class Products (

    @SerializedName("id")
    var productsID: Int? = null,

    @SerializedName("code")
    var productsCode: String? = null,

    @SerializedName("name")
    var productsName: String? = null,

    @SerializedName("quantity")
    var productsQTy: Int? = null,

    @SerializedName("imageUrl")
    var productsImg: String? = null,

    @SerializedName("error")
    var loginNetworkError: NetworkError = NetworkError(),


    @SerializedName("productCategoryID")
    var productCategoryID: Int = 0,


   var productsStockImg:  Uri = Uri.EMPTY,
    var productsStockImageCode: String = "",
    var productsStockImageName: String = "",
    var productsStockImagePath: String = "",

    var isProductSelected: Boolean = false,
    var isImageFromCamera: Boolean = false




){}
