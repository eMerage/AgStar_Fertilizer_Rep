package project.emarge.fertilizerrep.model.datamodel

import com.google.gson.annotations.SerializedName

data class NetworkError (

    var code: Int = 0,

    @SerializedName("code")
    var errorCode: String? = null,

    @SerializedName("description")
     var errorMessage: String? = null,

    @SerializedName("type")
     var errorTitle: String? = null ) {}

