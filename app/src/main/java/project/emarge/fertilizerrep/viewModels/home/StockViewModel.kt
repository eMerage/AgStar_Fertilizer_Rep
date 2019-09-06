package project.emarge.fertilizerrep.viewModels.home

import android.app.Application
import android.net.Uri
import android.widget.ProgressBar


import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import project.emarge.fertilizerrep.models.datamodel.Products
import project.emarge.fertilizerrep.models.datamodel.ProductsCategory


open class StockViewModel(application: Application) : AndroidViewModel(application) {



    var homeRepository: HomeRepo = HomeRepo(application)


    private var addedProductForStock : MutableLiveData<ArrayList<Products>>? = null

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


    fun addStockProducts(qty: Int, selectedImagefilePath : Uri, addedProduct: ArrayList<Products>,pro: Products,isCam : Boolean): MutableLiveData<ArrayList<Products>>{
        addedProductForStock = MutableLiveData<ArrayList<Products>>()
        addedProductForStock = homeRepository.addStock(qty,selectedImagefilePath,addedProduct,visitsID,pro,isCam)
        return addedProductForStock as MutableLiveData<ArrayList<Products>>
    }



    fun saveStockToServer(progressBar: ProgressBar) : MutableLiveData<Int>{
        return homeRepository.saveStock(progressBar,addedProductForStock?.value,visitsID)
    }





}