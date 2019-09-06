package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName
import project.emarge.fertilizerrep.model.datamodel.NetworkError

data class ProductsCategory (

    @SerializedName("id")
    var productsID: Int? = null,

    @SerializedName("productCategory")
    var productCategory: String? = null,

    @SerializedName("error")
    var loginNetworkError: NetworkError = NetworkError() ){}
