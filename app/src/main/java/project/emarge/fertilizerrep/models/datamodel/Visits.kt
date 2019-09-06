package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName
import project.emarge.fertilizerrep.model.datamodel.NetworkError


data class Visits(

    @SerializedName("id")
    var visitsID: Int? = null,

    @SerializedName("code")
    var visitsCode: String? = null,

    @SerializedName("createdDate")
    var visitsDate: String? = null,

    @SerializedName("dealerName")
    var visitsDealerName: String? = null,

    @SerializedName("dealerCode")
    var visitsDealerCode: String? = null,


    @SerializedName("order")
    var visitsOrder: Orders = Orders(),

    @SerializedName("visitPurpose")
    var visitsPurpose: ArrayList<Purpose>? = ArrayList<Purpose>(),



    @SerializedName("error")
    var loginNetworkError: NetworkError = NetworkError()
) {}
