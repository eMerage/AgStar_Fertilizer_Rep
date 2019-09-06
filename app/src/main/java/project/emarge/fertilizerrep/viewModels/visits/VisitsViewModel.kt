package project.emarge.fertilizerrep.viewModels.visits

import android.app.Application
import android.widget.RadioGroup

import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Dealer
import project.emarge.fertilizerrep.models.datamodel.Visits


open class VisitsViewModel(application: Application) : AndroidViewModel(application) {


    var visitsRepository: VisitsRepo = VisitsRepo(application)


    val isLoading = ObservableField<Boolean>()
    val editTextRemark = ObservableField<String>()
    val isRemarkEditTextVisibale = ObservableField<Boolean>()
    val addBtnVisibility = ObservableField<Boolean>()
    val visitsRespons = MutableLiveData<Visits>()
    var selectedDealer : Dealer = Dealer()
    var isCourtesy : Boolean = false







    fun setDealer(dealer : Dealer) {
        selectedDealer = dealer
    }


    fun getVisitsFromServer(): MutableLiveData<ArrayList<Visits>> {
        addBtnVisibility.set(true)
        return visitsRepository.geVisits(isLoading)
    }


    fun getDealersToVisits(location: LatLng): MutableLiveData<ArrayList<Dealer>> {
        return visitsRepository.geDealersToVisits(isLoading, location)
    }



    fun getSearchDealersToVisits(userInput: String,currentList : ArrayList<Dealer>): MutableLiveData<ArrayList<Dealer>> {
        return visitsRepository.geSearchDealers(userInput, currentList)
    }


    fun onIsCourtesyTypeChanged(radioGroup: RadioGroup, id: Int) {
        isCourtesy = if (id == R.id.radioButton1) {
            isRemarkEditTextVisibale.set(true)
            true
        } else {
            isRemarkEditTextVisibale.set(false)
            false
        }


    }


    fun onClickVisitsAdd() {
        visitsRepository.addVisits(visitsRespons,selectedDealer,isCourtesy,editTextRemark.get().toString(),addBtnVisibility)
    }

    fun uploadeMissingImages():MutableLiveData<Int>{
        return visitsRepository.getMissingImagesFromServer()
    }

}