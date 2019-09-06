package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName
import project.emarge.fertilizerrep.model.datamodel.NetworkError


data class Dealer (


    @SerializedName("id")
    var dealerID: Int? = null,

    @SerializedName("name")
    var dealerName: String? = null,

    @SerializedName("code")
    var dealerCode: String? = null,

    @SerializedName("contactNo")
    var dealerContactNumber: String? = null,


    @SerializedName("imageUrl")
    var dealerImg: String? = null,


    @SerializedName("latitude")
    var dealerLocationLan: Double? = null,


    @SerializedName("longtitude")
    var dealerLocationLon: Double? = null,

    @SerializedName("status")
    var status: Boolean = false,

    @SerializedName("user")
    var dealersRep: Rep = Rep(),


    @SerializedName("error")
    var loginNetworkError: NetworkError = NetworkError() ,

    var isDealerSelected: Boolean = false


    ){}
