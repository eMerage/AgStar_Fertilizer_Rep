package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName
import project.emarge.fertilizerrep.model.datamodel.NetworkError

data class Rep (

    @SerializedName("id")
    var userID: Int? = null,

    @SerializedName("name")
    var name: String? = null,


    @SerializedName("email")
    var email: String? = null,

    @SerializedName("imageUrl")
    var imageUrl: String? = null,


    @SerializedName("status")
    var userStatus: Boolean = false,


    @SerializedName("productsList")
    var productsList: ArrayList<Products>? = ArrayList<Products>(),


    @SerializedName("error")
    var loginNetworkError: NetworkError = NetworkError(),


    var isRepSelected: Boolean = false





){
}