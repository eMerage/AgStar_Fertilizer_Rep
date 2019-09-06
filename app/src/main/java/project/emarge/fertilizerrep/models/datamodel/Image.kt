package project.emarge.fertilizerrep.models.datamodel

import com.google.gson.annotations.SerializedName

data class Image (

    @SerializedName("id")
    var imageID: Int = 0,

    @SerializedName("name")
    var name: String = "",

    @SerializedName("imageUrl")
    var imageUrl: String = "",

    @SerializedName("imageCode")
    var imageCode: String = "",

    @SerializedName("status")
    var imagestatus: Boolean =false


){
}