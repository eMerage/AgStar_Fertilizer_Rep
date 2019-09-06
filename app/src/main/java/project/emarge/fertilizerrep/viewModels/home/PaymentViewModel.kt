package project.emarge.fertilizerrep.viewModels.home

import android.app.Application
import android.net.Uri
import android.widget.ProgressBar


import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import project.emarge.fertilizerrep.models.datamodel.Payment


open class PaymentViewModel(application: Application) : AndroidViewModel(application) {




    var homeRepository: HomeRepo = HomeRepo(application)

    private var paymentAddedRespons: MutableLiveData<ArrayList<Payment>>? = null



    private  var visitsID : Int = 0


    fun setVisistID(id : Int){
        visitsID=id
    }



        fun addPayment(addePayment: ArrayList<Payment>,OrderNumber : String,selectedPaymentType : String,OrderValue : String,selectedImagefilePath : Uri,isCam : Boolean,vCode : String): MutableLiveData<ArrayList<Payment>>{
        paymentAddedRespons = MutableLiveData<ArrayList<Payment>>()
        paymentAddedRespons = homeRepository.addPayments(addePayment,OrderNumber,selectedPaymentType,OrderValue,selectedImagefilePath,visitsID,isCam,vCode)
        return paymentAddedRespons as MutableLiveData<ArrayList<Payment>>
    }


    fun savePaymentToServer(progressBar: ProgressBar) : MutableLiveData<Int>{
        return homeRepository.savePayment(progressBar,paymentAddedRespons?.value,visitsID)
    }




}