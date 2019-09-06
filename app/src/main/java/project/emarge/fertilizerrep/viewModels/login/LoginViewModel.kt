package project.emarge.fertilizerrep.viewModels.login

import android.app.Application

import android.view.View
import android.widget.CheckBox

import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import project.emarge.fertilizerrep.models.datamodel.Rep


open class LoginViewModel(application: Application) : AndroidViewModel(application) {


    var textVersion = ObservableField<String>()
    var editTextPassword = MutableLiveData<String>()
    var editTextUserName = MutableLiveData<String>()

    var isRememberMeChecked = false
    var loginRepository: LoginRepo = LoginRepo(application)
    var loginValidationError = MutableLiveData<String>()
    private var loginRespond: MutableLiveData<Rep>? = null


    private var userCredential: MutableLiveData<Boolean>? = null

    val isLoading = ObservableField<Boolean>()
    val isButtonVisibale = ObservableField<Boolean>()




    fun checkUserCredential(): MutableLiveData<Boolean>  {
        isButtonVisibale.set(true)
        return loginRepository.getUSerSaveCredential()
    }


    fun loginValidation() {
       var error = loginRepository.loginValidationRepo(editTextUserName, editTextPassword)
       loginValidationError.value = error.value
    }

    fun getLoginResponsFromServer(): MutableLiveData<Rep> {
        return loginRepository.getUserDetails(editTextUserName,editTextPassword,isRememberMeChecked,isLoading,isButtonVisibale)
    }




    fun onRememberMeCheckedChanged(v: View) {
        isRememberMeChecked = (v as CheckBox).isChecked
    }


    fun setApplicationVersion() {
        var version = loginRepository.getAppVersionRepo()
        textVersion.set(version.value)
    }


}