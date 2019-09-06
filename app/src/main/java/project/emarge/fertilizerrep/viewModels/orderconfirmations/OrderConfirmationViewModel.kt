package project.emarge.fertilizerrep.viewModels.orderconfirmations

import android.app.Application
import androidx.databinding.ObservableField

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import project.emarge.fertilizerrep.models.datamodel.Orders


open class OrderConfirmationViewModel(application: Application) : AndroidViewModel(application) {

    var orderConfirmationRepository: OrderConfirmationRepo = OrderConfirmationRepo(application)
    val isLoading = ObservableField<Boolean>()
    val ordersRespons = MutableLiveData<Orders>()

    val reSendRespons = MutableLiveData<Orders>()

    fun getOrdersFromServer(): MutableLiveData<ArrayList<Orders>> {
        return orderConfirmationRepository.geNotConfrimOrders(isLoading)
    }


    fun setOrderConfrimation(oid : Int,code : String) {
        return orderConfirmationRepository.orderComfirmation(ordersRespons,oid,code,isLoading)
    }


    fun setReSendConfrimation(oid : Int) {
        return orderConfirmationRepository.orderComfirmationResend(reSendRespons,oid,isLoading)
    }



}