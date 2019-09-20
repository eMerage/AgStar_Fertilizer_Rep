package project.emarge.fertilizerrep.viewModels.home

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import project.emarge.fertilizerrep.models.datamodel.*
import project.emarge.fertilizerrep.services.network.NetworkErrorHandler
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import okhttp3.RequestBody
import okhttp3.MultipartBody

import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.util.regex.Pattern


class HomeRepo(application: Application) {


    var encryptedPreferences: EncryptedPreferences =
        EncryptedPreferences.Builder(application as Context?).withEncryptionPassword("122547895511")
            .build()
    private val USER_ID = "userID"
    private val USER_REMEMBER = "userRemember"

    var app: Application = application
    var networkErrorHandler: NetworkErrorHandler = NetworkErrorHandler()

    var apiInterface: APIInterface = ApiClient.client(application)


    val userID = encryptedPreferences.getInt(USER_ID, 0)

    val addedProducts = MutableLiveData<ArrayList<Products>>()

    val addedComplainLsit = MutableLiveData<ArrayList<Complain>>()

    val addedPaymentList = MutableLiveData<ArrayList<Payment>>()

    fun getProducts(cat: Int): MutableLiveData<ArrayList<Products>> {
        val datagetProducts = MutableLiveData<ArrayList<Products>>()
        var listPro = ArrayList<Products>()

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information ",
                Toast.LENGTH_LONG
            )
                .show()
        } else {

        }


        apiInterface.getProducts(cat, userID)
            .subscribeOn(Schedulers.io())
            .doOnError { it }
            .doOnTerminate { }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<Products>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(log: ArrayList<Products>) {
                    listPro = log

                }

                override fun onError(e: Throwable) {
                    Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    datagetProducts.postValue(listPro)

                }
            })

        return datagetProducts
    }


    fun getserachProducts(
        input: String,
        listAllProducts: ArrayList<Products>
    ): MutableLiveData<ArrayList<Products>> {
        val datagetProducts = MutableLiveData<ArrayList<Products>>()
        var listPro = ArrayList<Products>()

        if (input == "") {
            listPro.addAll(listAllProducts)
        } else {
            for (item in listAllProducts) {
                if (item.productsCode == input) {
                    listPro.add(item)

                }
            }
            try {
                if (listPro.isEmpty()) {
                    for (item in listAllProducts) {
                        var listProName = item.productsCode
                        var patternProName = input
                        var pattern = Pattern.compile(patternProName, Pattern.CASE_INSENSITIVE)
                        var matcher = pattern.matcher(listProName)
                        if (matcher.lookingAt()) {
                            listPro.add(item)
                        }
                    }

                }
            } catch (ex: java.lang.Exception) {


            }


        }

        datagetProducts.postValue(listPro)
        return datagetProducts
    }


    fun getProductsCategory(): MutableLiveData<ArrayList<ProductsCategory>> {
        val data = MutableLiveData<ArrayList<ProductsCategory>>()
        var listProCat = ArrayList<ProductsCategory>()



        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information ",
                Toast.LENGTH_LONG
            )
                .show()
        } else {

        }

        apiInterface.getProductCategories(5050)
            .subscribeOn(Schedulers.io())
            .doOnError { it }
            .doOnTerminate { }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<ProductsCategory>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(log: ArrayList<ProductsCategory>) {
                    listProCat = log

                }

                override fun onError(e: Throwable) {
                    Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    data.postValue(listProCat)
                }
            })

        return data
    }

    fun addProducts(
        products: Products,
        addedProduct: ArrayList<Products>
    ): MutableLiveData<ArrayList<Products>> {

        if (addedProduct.contains(products)) {
            Toast.makeText(app, "Product Already added", Toast.LENGTH_LONG).show()
        } else {
            addedProduct.add(products)
            var listReps = ArrayList<Products>()
            listReps = addedProduct
            addedProducts.postValue(listReps)
        }
        return addedProducts

    }

    fun saveOrder(
        addedProduct: ArrayList<Products>?,
        visitID: Int,
        deliveryType: String,
        paymentType: String,
        DispatchDate: String,
        progressBar: ProgressBar
    ): MutableLiveData<Orders> {

        var respond = Orders()

        val today = Calendar.getInstance()
        today.add(Calendar.DATE, -1)

        val data = MutableLiveData<Orders>()
        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information ",
                Toast.LENGTH_LONG
            )
                .show()
        } else if (addedProduct == null) {
            Toast.makeText(app, "Please add Products ", Toast.LENGTH_LONG).show()
        } else if (deliveryType.isNullOrEmpty()) {
            Toast.makeText(app, "Please select Delivery type ", Toast.LENGTH_LONG).show()
        } else if (paymentType.isNullOrEmpty()) {
            Toast.makeText(app, "Please select Payment type ", Toast.LENGTH_LONG).show()
        } else if (DispatchDate == "") {
            Toast.makeText(app, "Please add Dispatch date ", Toast.LENGTH_LONG).show()
        } else if (SimpleDateFormat("MM/dd/yyyy").parse(DispatchDate).before(Date(today.timeInMillis))) {
            Toast.makeText(app, "Please select future date", Toast.LENGTH_LONG).show()
        } else {

            progressBar.visibility = View.VISIBLE
            val jsonObject = JsonObject()
            jsonObject.addProperty("VisitID", visitID)
            jsonObject.addProperty("DispatchType", deliveryType)
            jsonObject.addProperty("DispatchDate", DispatchDate)
            jsonObject.addProperty("PaymentType", paymentType)
            val locJsonArr = JsonArray()
            for (item in addedProduct) {
                val ob = JsonObject()
                ob.addProperty("ProductID", item.productsID)
                ob.addProperty("Quantity", item.productsQTy)
                locJsonArr.add(ob)
            }
            jsonObject.add("VisitProducts", locJsonArr)

            apiInterface.saveOrder(jsonObject)
                .subscribeOn(Schedulers.io())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Orders> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: Orders) {
                        respond = log
                    }

                    override fun onError(e: Throwable) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                        data.postValue(respond)
                    }
                })

        }


        return data
    }


    fun orderComfirmation(oid: Int, confimCode: String): MutableLiveData<Orders> {
        val dataOrders = MutableLiveData<Orders>()
        var listOrders = Orders()


        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information ",
                Toast.LENGTH_LONG
            )
                .show()
        } else if (confimCode.isNullOrEmpty()) {
            Toast.makeText(app, "Confirmation Code empty", Toast.LENGTH_LONG)
                .show()
        } else {


            val jsonObject = JsonObject()
            jsonObject.addProperty("ID", oid)
            jsonObject.addProperty("ConfirmationCode", confimCode)

            apiInterface.confirmOrder(jsonObject)
                .subscribeOn(Schedulers.io())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Orders> {
                    override fun onSubscribe(d: Disposable) {}
                    override fun onNext(log: Orders) {
                        listOrders = log
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onComplete() {
                        dataOrders.postValue(listOrders)
                    }
                })
        }

        return dataOrders
    }


    fun addPayments(
        addedPayments: ArrayList<Payment>,
        orderNumber: String,
        paymentType: String,
        value: String,
        selectedImagefilePath: Uri,
        visitID: Int, isCam: Boolean, vCode: String
    ): MutableLiveData<ArrayList<Payment>> {
        when {
            (orderNumber == "").or((orderNumber.isNullOrEmpty())) -> Toast.makeText(
                app,
                "Please add order Number ",
                Toast.LENGTH_LONG
            ).show()
            paymentType.isNullOrEmpty() -> Toast.makeText(
                app,
                "Please select Payment type ",
                Toast.LENGTH_LONG
            ).show()
            (value == "").or((value.isNullOrEmpty())) -> Toast.makeText(
                app,
                "Please add Value",
                Toast.LENGTH_LONG
            ).show()
            (value == "0").and((orderNumber != vCode)) -> Toast.makeText(
                app,
                "Please add valid value",
                Toast.LENGTH_LONG
            ).show()
            (paymentType == "Cheque").and((selectedImagefilePath == Uri.EMPTY)) -> Toast.makeText(
                app,
                "Please add Image",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                var pay = Payment()
                var filePath: String = ""
                pay.orderNumber = orderNumber
                pay.paymentType = paymentType
                pay.paymentValue = value.toDouble()
                if (selectedImagefilePath == Uri.EMPTY) {

                } else {
                    pay.isImageFromCamera = isCam
                    pay.paymentImage = selectedImagefilePath
                    pay.paymentImageCode = genarateImageCode(visitID)

                    try {
                        filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (isCam) {
                                    selectedImagefilePath.path.toString()
                                } else {
                                    addImagesUpKitKat(selectedImagefilePath)
                                }
                            } else {
                                addImagesUpKitKat(selectedImagefilePath)
                            }

                        } else {
                            addImages(selectedImagefilePath)
                        }
                    } catch (ex: Exception) {

                    }



                    if (filePath == "") {

                    } else {
                        val file = File(filePath)
                        pay.paymentImageName = file.name
                        pay.paymentImagePath = filePath
                    }
                }

                if ((selectedImagefilePath != Uri.EMPTY) && (filePath == "")) {
                    Toast.makeText(app, "Image capture error,Please try again", Toast.LENGTH_LONG)
                        .show()
                } else {

                    addedPayments.add(pay)
                    addedPaymentList.postValue(addedPayments)
                }

            }
        }

        return addedPaymentList

    }

    fun savePayment(
        progressBar: ProgressBar,
        addedPayment: ArrayList<Payment>?,
        visitID: Int
    ): MutableLiveData<Int> {
        val data = MutableLiveData<Int>()
        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information ",
                Toast.LENGTH_LONG
            )
                .show()
        } else if (addedPayment == null) {
            Toast.makeText(app, "Please add Payments ", Toast.LENGTH_LONG).show()
        } else {
            progressBar.visibility = View.VISIBLE

            val jsonObject = JsonObject()
            jsonObject.addProperty("ID", visitID)
            val locJsonArr = JsonArray()

            for (item in addedPayment) {
                val ob = JsonObject()
                ob.addProperty("OrderNo", item.orderNumber)
                ob.addProperty("PaymentType", item.paymentType)
                ob.addProperty("PaymentValue", item.paymentValue)
                ob.addProperty("ImageCode", item.paymentImageCode)
                ob.addProperty("ImageTypeID", 1)
                ob.addProperty("Name", item.paymentImageName)

                locJsonArr.add(ob)
            }
            jsonObject.add("PaymentsList", locJsonArr)

            apiInterface.savePaymentVisitWithImageDetails(jsonObject)
                .subscribeOn(Schedulers.io())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: Int) {
                        if (log != null) {
                            data.postValue(log)
                        } else {
                            Toast.makeText(
                                app,
                                "Payment Adding fail,Please try again ",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                    override fun onError(e: Throwable) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                        savePaymentImage(addedPayment)
                    }
                })

        }

        return data
    }


    fun savePaymentImage(addedPayment: ArrayList<Payment>?) {
        for (item in addedPayment!!) {
            if (item.paymentImageCode.isNullOrEmpty()) {
            } else {
                val file = File(item.paymentImagePath)
                val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
                val fileToUpload =
                    MultipartBody.Part.createFormData("imageFile", file.name, requestBody)
                apiInterface.saveImageFile(fileToUpload, item.paymentImageCode)
                    .subscribeOn(Schedulers.io())
                    .doOnError { it }
                    .doOnTerminate { }
                    .observeOn(AndroidSchedulers.mainThread())
                    .retry(5)
                    .subscribe(object : Observer<Image> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(log: Image) {
                        }

                        override fun onError(e: Throwable) {
                        }

                        override fun onComplete() {

                        }
                    })
            }

        }

    }


    fun addStock(
        qty: Int,
        selectedImagefilePath: Uri,
        addedProduct: ArrayList<Products>,
        visitID: Int,
        products: Products, isCam: Boolean
    ): MutableLiveData<ArrayList<Products>> {
        if (selectedImagefilePath == Uri.EMPTY) {
            Toast.makeText(app, "Please add image", Toast.LENGTH_LONG).show()
        } else if (qty == 0) {
            Toast.makeText(app, "Please add quantity", Toast.LENGTH_LONG).show()
        } else {
            var filePath: String = ""

            try {
                filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (isCam) {
                            selectedImagefilePath.path.toString()
                        } else {
                            addImagesUpKitKat(selectedImagefilePath)
                        }
                    } else {
                        addImagesUpKitKat(selectedImagefilePath)
                    }

                } else {
                    addImages(selectedImagefilePath)
                }
            } catch (ex: Exception) {

            }

            products.productsQTy = qty
            products.productsStockImg = selectedImagefilePath
            products.productsStockImageCode = genarateImageCode(visitID)
            products.isImageFromCamera = isCam

            if (filePath == "") {

            } else {
                val file = File(filePath)
                products.productsStockImageName = file.name
                products.productsStockImagePath = filePath
            }

            if (filePath == "") {
                Toast.makeText(app, "Image capture error,Please try again", Toast.LENGTH_LONG)
                    .show()
            } else {
                addedProduct.add(products)
                var listReps = ArrayList<Products>()
                listReps = addedProduct
                addedProducts.postValue(listReps)
            }

        }

        return addedProducts

    }


    fun saveStock(
        progressBar: ProgressBar,
        addedStock: ArrayList<Products>?,
        visitID: Int
    ): MutableLiveData<Int> {

        val data = MutableLiveData<Int>()

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information ",
                Toast.LENGTH_LONG
            )
                .show()
        } else if (addedStock == null) {
            Toast.makeText(app, "Please add Stock ", Toast.LENGTH_LONG).show()
        } else {
            progressBar.visibility = View.VISIBLE

            val jsonObject = JsonObject()
            jsonObject.addProperty("ID", visitID)
            val locJsonArr = JsonArray()

            for (item in addedStock) {
                val ob = JsonObject()
                ob.addProperty("ProductID", item.productsID)
                ob.addProperty("ImageCode", item.productsStockImageCode)
                ob.addProperty("Quantity", item.productsQTy)
                ob.addProperty("ImageTypeID", 2)
                ob.addProperty("Name", item.productsStockImageName)
                locJsonArr.add(ob)
            }
            jsonObject.add("VisitProductsList", locJsonArr)

            apiInterface.saveStockVisitWithImageDetails(jsonObject)
                .subscribeOn(Schedulers.io())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: Int) {
                        if (log != null) {
                            data.postValue(log)
                        } else {
                            Toast.makeText(
                                app,
                                "Stock Adding fail,Please try again ",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                        saveStockImage(addedStock)
                    }
                })

        }

        return data
    }


    fun saveStockImage(stock: ArrayList<Products>) {
        for (item in stock) {
            if (item.productsStockImageCode.isNullOrEmpty()) {
            } else {
                val file = File(item.productsStockImagePath)
                val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
                val fileToUpload =
                    MultipartBody.Part.createFormData("imageFile", file.name, requestBody)
                apiInterface.saveImageFile(fileToUpload, item.productsStockImageCode)
                    .subscribeOn(Schedulers.io())
                    .retry(5)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Image> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(log: Image) {
                        }

                        override fun onError(e: Throwable) {
                        }

                        override fun onComplete() {

                        }
                    })
            }
        }
    }


    fun addComplain(
        addedComplain: ArrayList<Complain>,
        product: Products,
        dec: String,
        invoiceNumber: String,
        batchNum: String,
        exDate: String,
        selectedImagefilePath: Uri,
        vID: Int, isCam: Boolean
    ):
            MutableLiveData<ArrayList<Complain>> {
        when {
            product.productsID == null -> Toast.makeText(
                app,
                "Please add product",
                Toast.LENGTH_LONG
            ).show()
            dec.isNullOrEmpty() -> Toast.makeText(
                app,
                "Please add Description",
                Toast.LENGTH_LONG
            ).show()
            invoiceNumber.isNullOrEmpty() -> Toast.makeText(
                app,
                "Please add Invoice Number",
                Toast.LENGTH_LONG
            ).show()
            batchNum.isNullOrEmpty() -> Toast.makeText(
                app,
                "Please add Batch Number",
                Toast.LENGTH_LONG
            ).show()
            else -> {
                var com = Complain()
                var filePath: String = ""

                com.complainProductsID = product.productsID
                com.complainDes = dec
                com.complainInvoiceNumber = invoiceNumber
                com.complainBatchNumber = batchNum
                com.complainExDate = exDate
                com.complainProductsName = product.productsName.toString()
                if (selectedImagefilePath == Uri.EMPTY) {

                } else {
                    com.complainImg = selectedImagefilePath
                    com.complainImgCode = genarateImageCode(vID)
                    com.complainProductsCode = product.productsCode.toString()
                    com.isImageFromCamera = isCam

                    try {
                        filePath = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (isCam) {
                                    selectedImagefilePath.path.toString()
                                } else {
                                    addImagesUpKitKat(selectedImagefilePath)
                                }
                            } else {
                                addImagesUpKitKat(selectedImagefilePath)
                            }

                        } else {
                            addImages(selectedImagefilePath)
                        }
                    } catch (ex: Exception) {

                    }

                    if (filePath == "") {

                    } else {
                        val file = File(filePath)
                        com.complainImageName = file.name
                        com.complainImagePath = filePath
                    }
                }

                if ((selectedImagefilePath != Uri.EMPTY) && (filePath == "")) {
                    Toast.makeText(app, "Image capture error,Please try again", Toast.LENGTH_LONG)
                        .show()
                } else {
                    addedComplain.add(com)
                    addedComplainLsit.postValue(addedComplain)
                }

            }
        }

        return addedComplainLsit

    }


    fun saveComplain(
        progressBar: ProgressBar,
        addedComplain: ArrayList<Complain>?,
        visitID: Int
    ): MutableLiveData<Int> {

        val data = MutableLiveData<Int>()

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(
                app,
                "No internet connection you will miss the latest information",
                Toast.LENGTH_LONG
            )
                .show()
        } else if (addedComplain == null) {
            Toast.makeText(app, "Please add Complain ", Toast.LENGTH_LONG).show()
        } else {
            progressBar.visibility = View.VISIBLE

            val jsonObject = JsonObject()
            jsonObject.addProperty("ID", visitID)
            val locJsonArr = JsonArray()

            for (item in addedComplain) {
                val ob = JsonObject()
                ob.addProperty("ProductID", item.complainProductsID)
                ob.addProperty("Description", item.complainDes)
                ob.addProperty("InvoiceNo", item.complainInvoiceNumber)
                ob.addProperty("BatchNo", item.complainBatchNumber)
                ob.addProperty("ExpiryDate", item.complainExDate)
                ob.addProperty("ImageCode", item.complainImgCode)

                ob.addProperty("ImageTypeID", 3)
                ob.addProperty("Name", item.complainImageName)
                locJsonArr.add(ob)
            }
            jsonObject.add("ComplainsList", locJsonArr)


            apiInterface.saveComplainVisitWithImageDetails(jsonObject)
                .subscribeOn(Schedulers.io())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: Int) {
                        if (log != null) {
                            data.postValue(log)
                        } else {
                            Toast.makeText(
                                app,
                                "Complain Adding fail,Please try again ",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG)
                            .show()
                    }

                    override fun onComplete() {
                        progressBar.visibility = View.GONE
                        saveComplainImage(addedComplain)
                    }
                })

        }

        return data
    }


    fun saveComplainImage(complain: ArrayList<Complain>) {
        for (item in complain) {
            if (item.complainImgCode.isNullOrEmpty()) {
            } else {
                val file = File(item.complainImagePath)
                val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
                val fileToUpload =
                    MultipartBody.Part.createFormData("imageFile", file.name, requestBody)
                apiInterface.saveImageFile(fileToUpload, item.complainImgCode)
                    .subscribeOn(Schedulers.io())
                    .retry(5)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Image> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(log: Image) {
                        }

                        override fun onError(e: Throwable) {
                        }

                        override fun onComplete() {

                        }
                    })
            }
        }


    }


    fun genarateImageCode(vid: Int): String {
        val c = Calendar.getInstance()
        val numberFromTime =
            c.get(Calendar.YEAR).toString() + c.get(Calendar.DATE).toString() + c.get(Calendar.HOUR).toString() + c.get(
                Calendar.MINUTE
            ).toString() + c.get(Calendar.SECOND).toString() + c.get(Calendar.MILLISECOND).toString()
        val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var count = 3
        val builder = StringBuilder()
        while (count-- != 0) {
            val character = (Math.random() * ALPHA_NUMERIC_STRING.length).toInt()
            builder.append(ALPHA_NUMERIC_STRING[character])

        }
        return userID.toString() + vid + numberFromTime + builder.toString()
    }


    fun getRealPathFromURI(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)!!
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()

            return if (cursor.getString(column_index) == null) {
                ""
            } else {
                cursor.getString(column_index)
            }

        } finally {
            cursor?.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun addImagesUpKitKat(data: Uri): String {
        var filep: String = ""
        if (data == null) {
            Toast.makeText(app, "Please select image from gallery", Toast.LENGTH_LONG).show()
        } else {
            filep = getPath(app, data)
        }
        return filep
    }


    private fun addImages(data: Uri?): String {
        var filep: String = ""
        if (data == null) {
            Toast.makeText(app, "Please select image from gallery", Toast.LENGTH_LONG).show()
        } else {
            filep = getRealPathFromURI(app, data)
        }
        return filep

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun getPath(context: Context, uri: Uri): String {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split =
                    docId.split((":").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return (Environment.getExternalStorageDirectory().toString() + "/" + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )

                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                var docId = DocumentsContract.getDocumentId(uri)
                var split =
                    docId.split((":").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                var type = split[0]
                var contentUri: Uri = Uri.EMPTY
                when (type) {
                    "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf<String>(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }

        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            if (isGooglePhotosUri(uri))
                return uri.lastPathSegment.toString()
            return this.getDataColumn(context, uri, null, null).toString()
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path.toString()
        }

        return ""
    }


    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String {

        lateinit var cursor: Cursor
        val column = "_data"
        val projection = arrayOf<String>(column)
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)!!
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor.close()
        }
        return ""
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri - The Uri to check.
     * @return - Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }


}