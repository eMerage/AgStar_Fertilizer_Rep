package project.emarge.fertilizerrep.viewModels.home

import android.app.Application
import android.widget.ProgressBar


import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import project.emarge.fertilizerrep.models.datamodel.Orders
import project.emarge.fertilizerrep.models.datamodel.Products
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory


open class OrderViewModel(application: Application) : AndroidViewModel(application) {


    private val _index = MutableLiveData<Int>()


    var homeRepository: HomeRepo = HomeRepo(application)





    private var addedProductForOrder: MutableLiveData<ArrayList<Products>>? = null

    val orderSaveRespons = MutableLiveData<Int>()


    private  var visitsID : Int = 0


    fun setVisistID(id : Int){
        visitsID=id

    }


    fun getProducts(category: Int): MutableLiveData<ArrayList<Products>> {
        return homeRepository.getProducts(category)
    }


    fun getSearchProducts(userinputy : String,list : ArrayList<Products>): MutableLiveData<ArrayList<Products>> {
        return homeRepository.getserachProducts(userinputy,list)
    }



    fun getProductCategory(): MutableLiveData<ArrayList<ProductsCategory>>{
        return homeRepository.getProductsCategory()
    }

    fun addProducts(product: Products,addedProduct: ArrayList<Products>): MutableLiveData<ArrayList<Products>>{
        addedProductForOrder = MutableLiveData<ArrayList<Products>>()
        addedProductForOrder = homeRepository.addProducts(product,addedProduct)
        return addedProductForOrder as MutableLiveData<ArrayList<Products>>
    }



    fun setOrderComfirmation(oid : Int,confimCode : String): MutableLiveData<Orders>{
        return homeRepository.orderComfirmation(oid,confimCode)
    }



    fun saveOrderToServer(selectedDeliveryType : String,selectedPaymentType : String,dispatchDate : String,progressBar: ProgressBar): MutableLiveData<Orders>{
        return homeRepository.saveOrder(addedProductForOrder?.value,visitsID,selectedDeliveryType,selectedPaymentType,dispatchDate,progressBar)
    }





}