package project.emarge.fertilizerrep.viewModels.visits

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import io.reactivex.Observer
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import project.emarge.fertilizerrep.models.datamodel.Dealer
import project.emarge.fertilizerrep.models.datamodel.Image

import project.emarge.fertilizerrep.models.datamodel.Visits
import project.emarge.fertilizerrep.services.network.NetworkErrorHandler
import java.io.File
import java.util.*
import java.util.regex.Pattern

class VisitsRepo(application: Application) {


    var app: Application = application
    var networkErrorHandler: NetworkErrorHandler = NetworkErrorHandler()

    var apiInterface: APIInterface = ApiClient.client(application)


    var encryptedPreferences: EncryptedPreferences =
        EncryptedPreferences.Builder(application).withEncryptionPassword("122547895511").build()
    private val USER_ID = "userID"

    val userID = encryptedPreferences.getInt(USER_ID, 0)


    fun geVisits(lod: ObservableField<Boolean>): MutableLiveData<ArrayList<Visits>> {

        val dataVisits = MutableLiveData<ArrayList<Visits>>()
        var listVisits = ArrayList<Visits>()

        lod.set(true)

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(app, "No internet connection you will miss the latest information ", Toast.LENGTH_LONG)
                .show()
        } else {

        }

        apiInterface.getVisitsByRep(userID)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<Visits>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(log: ArrayList<Visits>) {
                    listVisits = log

                }

                override fun onError(e: Throwable) {
                    lod.set(false)
                    Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    dataVisits.postValue(listVisits)
                    lod.set(false)
                }
            })

        return dataVisits

    }


    fun addVisits(
        visisRespond: MutableLiveData<Visits>,
        dealer: Dealer,
        isCourtesy: Boolean,
        remark: String,
        btnVisible: ObservableField<Boolean>
    ) {

        var visits = Visits()

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(app, "No internet connection you will miss the latest information ", Toast.LENGTH_LONG)
                .show()
        } else if (dealer.dealerID == null) {
           Toast.makeText(app, "please selecte the Dealer", Toast.LENGTH_LONG).show()
        } else {

            btnVisible.set(false)

            val jsonObject = JsonObject()
            jsonObject.addProperty("AppCode", crateVisitsMobileCode(dealer))
            jsonObject.addProperty("RepID", userID.toString())
            jsonObject.addProperty("DealerID", dealer.dealerID)
            jsonObject.addProperty("IsCourtesy", isCourtesy)
            jsonObject.addProperty("Remark", remark)


           apiInterface.saveVisit(jsonObject)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Visits> {
                    override fun onSubscribe(d: Disposable) {
                    }
                    override fun onNext(log: Visits) {
                        visits = log
                    }
                    override fun onError(e: Throwable) {
                        btnVisible.set(true)
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                    }
                    override fun onComplete() {
                        visisRespond.postValue(visits)
                        btnVisible.set(false)
                    }
                })




        }

    }


    fun geSearchDealers(userInput: String, currentList: ArrayList<Dealer>): MutableLiveData<ArrayList<Dealer>> {
        val dataDealersVisits = MutableLiveData<ArrayList<Dealer>>()
        var listDealers = ArrayList<Dealer>()

        if(userInput.isNullOrEmpty()){
            dataDealersVisits.postValue(currentList)
        }else{
            for (dealer in currentList) {
                var listDealerName = dealer.dealerName
                var patternName = userInput


                var pattern = Pattern.compile(patternName, Pattern.CASE_INSENSITIVE)
                var matcher = pattern.matcher(listDealerName)

                if (matcher.lookingAt()) {
                    listDealers.add(dealer)
                }



            }
            if(listDealers.isEmpty()){
                for (dealer in currentList) {
                    var listDealerName = dealer.dealerCode
                    var patternName = userInput
                    var pattern = Pattern.compile(patternName, Pattern.CASE_INSENSITIVE)
                    var matcher = pattern.matcher(listDealerName)
                    if (matcher.lookingAt()) {
                        listDealers.add(dealer)
                    }
                }

            }
            dataDealersVisits.postValue(listDealers)
        }


        return dataDealersVisits

    }


    fun geDealersToVisits(lod: ObservableField<Boolean>, location: LatLng): MutableLiveData<ArrayList<Dealer>> {
        val dataDealersVisits = MutableLiveData<ArrayList<Dealer>>()
        var listDealers = ArrayList<Dealer>()

        lod.set(true)

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(app, "No internet connection you will miss the latest information ", Toast.LENGTH_LONG)
                .show()
        } else {

        }

        apiInterface.getDealersByRepNearLocation(userID, location.latitude, location.longitude)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<Dealer>> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(log: ArrayList<Dealer>) {
                    listDealers = log
                }

                override fun onError(e: Throwable) {
                    lod.set(false)
                    Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                }

                override fun onComplete() {
                    dataDealersVisits.postValue(listDealers)
                    lod.set(false)

                }
            })

        return dataDealersVisits

    }

    fun getMissingImagesFromServer() :MutableLiveData<Int>{


        val imagecount = MutableLiveData<Int>()

        if (!app.isConnectedToNetwork()) {

        } else {
            var listImages = ArrayList<Image>()

            apiInterface.getmissingImages(userID)
                .subscribeOn(Schedulers.newThread())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<Image>> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: ArrayList<Image>) {
                        listImages = log

                    }

                    override fun onError(e: Throwable) {
                        println("getMissingImagesFromServer "+e)
                    }

                    override fun onComplete() {
                        getImages(listImages)
                        imagecount.postValue(listImages.size)
                    }
                })

        }

        return  imagecount
    }


    fun getImages(listImages: ArrayList<Image>) {

        println("fffffffffffffffff  ll")

        var misingImagesPath = ArrayList<Image>()
        var arrIntranalImages = getImagesFromIntranalStorage()
        for ((index, item) in arrIntranalImages.withIndex()) {
            val file = File(arrIntranalImages[index])
            for(imagess in listImages){
                if(file.name == imagess.name){
                    misingImagesPath.add(Image(imagess.imageID,imagess.name,arrIntranalImages[index].toString(),imagess.imageCode))
                }else{

                }
            }
        }

        val storageDir: File = app?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if(storageDir == null){
        }else{
            var filePathOfImage = storageDir.path
            for(item in  storageDir.list()){
                for(imagess in listImages){
                    if(item == imagess.name){
                        var path = "$filePathOfImage/$item"
                        misingImagesPath.add(Image(imagess.imageID,imagess.name,path,imagess.imageCode))
                    }else{

                    }
                }

            }
        }

        uploademissingImagesToServer(misingImagesPath)

    }


    fun uploademissingImagesToServer(list: ArrayList<Image>){
        for(imagemissing in list){
            val file = File(imagemissing.imageUrl)
            val requestBody = RequestBody.create(MediaType.parse("*/*"), file)
            val fileToUpload = MultipartBody.Part.createFormData("imageFile", file.name, requestBody)
            apiInterface.saveImageFile(fileToUpload, imagemissing.imageCode)
                .subscribeOn(Schedulers.io())
                .doOnError {}
                .repeat(5)
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Image> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: Image) {

                    }
                    override fun onError(e: Throwable) {
                        println("uploademissingImagesToServer "+e)

                    }
                    override fun onComplete() {
                    }
                })
        }

    }

    private fun getImagesFromIntranalStorage(): Array<String?> {
        val proj = arrayOf<String>(MediaStore.Images.Media.DATA)
        val orderBy = MediaStore.Images.Media._ID

        var cursor = app?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            proj,
            null,
            null,
            orderBy
        )
        var count = cursor?.count
        var arrPath = arrayOfNulls<String>(count!!)
        for (i in 0 until count) {
            cursor?.moveToPosition(i)
            val dataColumnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
            arrPath[i] = cursor?.getString(dataColumnIndex!!)

        }
        return arrPath
    }




    fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
    }


    fun crateVisitsMobileCode(dealer: Dealer): String {
        val c = Calendar.getInstance()
        val numberFromTime =
            c.get(Calendar.DATE).toString() + c.get(Calendar.HOUR).toString() + c.get(Calendar.MINUTE).toString()
        val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var count = 3
        val builder = StringBuilder()
        while (count-- != 0) {
            val character = (Math.random() * ALPHA_NUMERIC_STRING.length).toInt()
            builder.append(ALPHA_NUMERIC_STRING[character])

        }

        return userID.toString() + dealer.dealerCode + numberFromTime + builder.toString()
    }

}