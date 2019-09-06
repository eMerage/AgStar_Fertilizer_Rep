package project.emarge.fertilizerrep.viewModels.home

import android.app.Application
import android.net.Uri
import android.widget.ProgressBar


import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import project.emarge.fertilizerrep.models.datamodel.Complain
import project.emarge.fertilizerrep.models.datamodel.Products
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory


open class ComplaintViewModel(application: Application) : AndroidViewModel(application) {

    var homeRepository: HomeRepo = HomeRepo(application)


    private var addedComplainList : MutableLiveData<ArrayList<Complain>>? = null


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

    fun addComplainProducts(addedComplain: ArrayList<Complain>,product: Products,dec : String,invoiceNumber : String,batchNum : String ,exDate : String,selectedImagefilePath : Uri,isCam : Boolean):
            MutableLiveData<ArrayList<Complain>>{
        addedComplainList = MutableLiveData<ArrayList<Complain>>()
        addedComplainList = homeRepository.addComplain(addedComplain,product,dec,invoiceNumber,batchNum,exDate,selectedImagefilePath,visitsID,isCam)
        return addedComplainList as MutableLiveData<ArrayList<Complain>>
    }



    fun saveComplainToServer(progressBar: ProgressBar) : MutableLiveData<Int>{
        return homeRepository.saveComplain(progressBar,addedComplainList?.value,visitsID)
    }



}