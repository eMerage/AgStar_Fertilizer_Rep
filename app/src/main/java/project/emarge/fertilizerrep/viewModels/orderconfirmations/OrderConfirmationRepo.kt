package project.emarge.fertilizerrep.viewModels.orderconfirmations

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import io.reactivex.Observer
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import project.emarge.fertilizerrep.models.datamodel.Orders

import project.emarge.fertilizerrep.services.network.NetworkErrorHandler
import java.util.*

class OrderConfirmationRepo(application: Application) {


    var app: Application = application
    var networkErrorHandler: NetworkErrorHandler = NetworkErrorHandler()

    var apiInterface: APIInterface = ApiClient.client(application)


    var encryptedPreferences: EncryptedPreferences =
            EncryptedPreferences.Builder(application).withEncryptionPassword("122547895511").build()
    private val USER_ID = "userID"

    val userID = encryptedPreferences.getInt(USER_ID, 0)


    fun orderComfirmationResend(resendRespond: MutableLiveData<Orders>,oid: Int, lod: ObservableField<Boolean>) {
        var listOrders = Orders()
        if (!app.isConnectedToNetwork()) {
            Toast.makeText(app, "No internet connection you will miss the latest information", Toast.LENGTH_LONG).show()
        } else {
            lod.set(true)
            apiInterface.resendConfirmationCode(oid)
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
                            lod.set(false)
                           // Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                        }

                        override fun onComplete() {
                            lod.set(false)
                            resendRespond.postValue(listOrders)
                        }
                    })
        }


    }


    fun orderComfirmation(respond: MutableLiveData<Orders>, oid: Int, confimCode: String, lod: ObservableField<Boolean>) {
        var listOrders = Orders()
        if (!app.isConnectedToNetwork()) {
            Toast.makeText(app, "No internet connection you will miss the latest information ", Toast.LENGTH_LONG).show()
        } else if (confimCode.isNullOrEmpty()) {
            Toast.makeText(app, "Confirmation Code empty", Toast.LENGTH_LONG).show()
        } else {

            lod.set(true)

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
                            lod.set(false)
                            Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()

                        }

                        override fun onComplete() {
                            lod.set(false)
                            respond.postValue(listOrders)

                        }
                    })
        }


    }


    fun geNotConfrimOrders(lod: ObservableField<Boolean>): MutableLiveData<ArrayList<Orders>> {
        val dataOrders = MutableLiveData<ArrayList<Orders>>()
        var listOrders = ArrayList<Orders>()

        lod.set(true)

        if (!app.isConnectedToNetwork()) {
            Toast.makeText(app, "No internet connection you will miss the latest information ", Toast.LENGTH_LONG).show()
        } else {

        }

        apiInterface.getOrdersIncomplete(userID)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<Orders>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(log: ArrayList<Orders>) {
                        listOrders = log

                    }

                    override fun onError(e: Throwable) {
                        lod.set(false)
                        Toast.makeText(app, networkErrorHandler(e).errorTitle, Toast.LENGTH_LONG).show()
                    }

                    override fun onComplete() {

                            lod.set(false)
                            dataOrders.postValue(listOrders)

                    }
                })

        return dataOrders

    }


    fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
    }


}