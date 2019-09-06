package project.emarge.fertilizerrep.models.datamodel

import android.net.Uri


data class Complain(

    var complainProductsID: Int? = null,
    var complainProductsCode: String = "",
    var complainDes: String = "",
    var complainInvoiceNumber: String = "",
    var complainBatchNumber: String = "",
    var complainExDate: String = "",
    var complainImg: Uri? = Uri.EMPTY,
    var complainImgCode: String = "",
    var complainImageName: String = "",
    var complainImagePath: String = "",
    var isImageFromCamera: Boolean = false,
    var complainProductsName: String = ""


) {}
