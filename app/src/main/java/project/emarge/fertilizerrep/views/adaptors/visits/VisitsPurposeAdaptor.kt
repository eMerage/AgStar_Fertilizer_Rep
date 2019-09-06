package project.emarge.fertilizerrep.views.adaptor.visits

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_purpose.view.*
import project.emarge.fertilizerrep.R

import project.emarge.fertilizerrep.models.datamodel.Purpose


class VisitsPurposeAdaptor(val items: ArrayList<Purpose>, val context: Context) :
    RecyclerView.Adapter<VisitsPurposeAdaptor.ViewHolderVisitsProducts>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderVisitsProducts {
        return ViewHolderVisitsProducts(
            LayoutInflater.from(context).inflate(
                R.layout.listview_purpose,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolderVisitsProducts, position: Int) {
        var itemPurpose = items[position]
        when {
            itemPurpose.purposeCode == "ORDER" -> {
                holder.ImageViewPurposeImage?.setImageResource(R.drawable.ic_round_dark_green)
                holder.textviewPurpose?.text = "O"
            }
            itemPurpose.purposeCode == "CMPLN" -> {
                holder.ImageViewPurposeImage?.setImageResource(R.drawable.ic_round_light_read)
                holder.textviewPurpose?.text = "CO"
            }
            itemPurpose.purposeCode == "PYMNT" -> {
                holder.ImageViewPurposeImage?.setImageResource(R.drawable.ic_round_light_blue)
                holder.textviewPurpose?.text = "P"
            }
            itemPurpose.purposeCode == "STOCK" -> {
                holder.ImageViewPurposeImage?.setImageResource(R.drawable.ic_round_light_green)
                holder.textviewPurpose?.text = "S"
            }
            itemPurpose.purposeCode == "CRTSY" -> {
                holder.ImageViewPurposeImage?.setImageResource(R.drawable.ic_round_light_yellow)
                holder.textviewPurpose?.text = "C"
            }
        }


    }




inner class ViewHolderVisitsProducts(view: View) : RecyclerView.ViewHolder(view) {
    val ImageViewPurposeImage = view.ImageView_purpose_image
    val textviewPurpose = view.textview_purpose

}
}