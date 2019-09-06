package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName

data class Purpose  (
    @SerializedName("id")
    var purposeID: Int = 0,

    @SerializedName("code")
    var purposeCode: String = "",

    @SerializedName("name")
    var purposeName: String = ""

){}
