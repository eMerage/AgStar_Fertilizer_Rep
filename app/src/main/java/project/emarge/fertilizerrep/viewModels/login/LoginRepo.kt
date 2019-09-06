package project.emarge.fertilizerrep.viewModels.login

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import emarge.project.caloriecaffe.network.api.APIInterface
import emarge.project.caloriecaffe.network.api.ApiClient
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Rep
import project.emarge.fertilizerrep.services.network.NetworkErrorHandler



class LoginRepo(application: Application) {


    var encryptedPreferences: EncryptedPreferences=EncryptedPreferences.Builder(application).withEncryptionPassword("122547895511").build()
    private val USER_ID = "userID"
    private val USER_REMEMBER = "userRemember"

    var app: Application = application
    var networkErrorHandler: NetworkErrorHandler = NetworkErrorHandler()

    var apiInterface: APIInterface = ApiClient.client(application)


    fun loginValidationRepo(
        userName: MutableLiveData<String>,
        password: MutableLiveData<String>
    ): MutableLiveData<String> {
        var validationErrorMsg = MutableLiveData<String>()
        var error: String = when {
            !app.isConnectedToNetwork() -> "No internet connection !"
            userName.value.isNullOrEmpty() -> "Please Enter your User Name !"
            password.value.isNullOrEmpty() -> "Please Enter your password !"
            else -> {
                "ok"
            }
        }
        validationErrorMsg.value = error
        return validationErrorMsg

    }

    fun getAppVersionRepo(): MutableLiveData<String> {
        var appVersion = MutableLiveData<String>()
        var appLevel: String =app.getString(R.string.app_name)
        var pInfo: PackageInfo? = null
        try {
            pInfo = app.packageManager.getPackageInfo(app.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            appVersion.value = appLevel + "1.0.0"
            e.printStackTrace()
        }
        appVersion.value = appLevel + pInfo!!.versionName
        return appVersion
    }


    fun getUSerSaveCredential(): MutableLiveData<Boolean> {
        var isUserLogin = MutableLiveData<Boolean>()
        val userRemember = encryptedPreferences.getBoolean(USER_REMEMBER, false)
        val userId = encryptedPreferences.getString(USER_ID, "")
        isUserLogin.value = (userId.isNotEmpty()) && (userRemember)
        return isUserLogin

    }


    fun getUserDetails(
        user: MutableLiveData<String>, password: MutableLiveData<String>, isRememberMeChecked: Boolean,
        isLoading: ObservableField<Boolean>, buttonVisibale: ObservableField<Boolean>
    ): MutableLiveData<Rep> {
        val data = MutableLiveData<Rep>()
        var loginUser = Rep()

        isLoading.set(true)
        buttonVisibale.set(false)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            apiInterface.validateUser(user.value.toString(), password.value.toString(), 2, task.result?.token!!)
                .subscribeOn(Schedulers.io())
                .doOnError { it }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Rep> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(log: Rep) {
                        loginUser = log
                        data.postValue(loginUser)
                        if (isRememberMeChecked) {
                            encryptedPreferences.edit().putBoolean(USER_REMEMBER, true).apply()
                        } else {
                           encryptedPreferences.edit().putBoolean(USER_REMEMBER, false).apply()
                        }

                    }

                    override fun onError(e: Throwable) {
                        isLoading.set(false)
                        buttonVisibale.set(true)
                        loginUser.loginNetworkError = networkErrorHandler(e)
                        data.postValue(loginUser)
                    }

                    override fun onComplete() {
                        if(loginUser.userStatus){
                            encryptedPreferences.edit().putInt(USER_ID, loginUser.userID!!).apply()
                        }else{

                        }

                        isLoading.set(false)
                        buttonVisibale.set(true)
                    }
                })
        })

        return data
    }


    fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }


}