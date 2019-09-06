package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName
import project.emarge.fertilizerrep.model.datamodel.NetworkError


data class Orders (

    @SerializedName("id")
    var orderID: Int = 0,

    @SerializedName("code")
    var orderCode: String = "",

    @SerializedName("dispatchDate")
    var orderDispatchDate: String = "",

    @SerializedName("createdDate")
    var orderDate: String = "",

    @SerializedName("dispatchType")
    var orderDispatchType: String = "",

    @SerializedName("paymentType")
    var orderPaymentType: String = "",

    @SerializedName("visitsPurpose")
    var isOrderComplete: Boolean = false,

    @SerializedName("isOrderConfirmed")
    var isOrderConfirmed: Boolean = false,

    @SerializedName("visitProducts")
    var productsList: ArrayList<Products> = ArrayList<Products>(),

    @SerializedName("status")
    var status: Boolean = false,

    @SerializedName("visitCode")
    var visitsCode: String = "",

    @SerializedName("dealerCode")
    var dealerCode: String = "",

    @SerializedName("dealerName")
    var dealerName: String = "",


    @SerializedName("error")
    var networkError: NetworkError = NetworkError() ){}
