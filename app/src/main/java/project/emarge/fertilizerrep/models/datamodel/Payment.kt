package project.emarge.fertilizerrep.models.datamodel

import android.net.Uri


data class Payment (
    var orderNumber: String = "",
    var paymentType: String = "",
    var paymentValue: Double = 0.0,
    var paymentImageCode: String = "",
    var paymentImage: Uri = Uri.EMPTY,
    var paymentImageName: String = "",
    var paymentImagePath: String = "",
    var isImageFromCamera: Boolean = false

){}
