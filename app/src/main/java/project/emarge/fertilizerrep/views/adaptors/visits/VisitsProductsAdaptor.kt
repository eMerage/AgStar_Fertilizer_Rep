package project.emarge.fertilizerrep.views.adaptor.visits

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_visits_products.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Products


class VisitsProductsAdaptor(val items: ArrayList<Products>, val context: Context) :
    RecyclerView.Adapter<VisitsProductsAdaptor.ViewHolderVisitsProducts>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderVisitsProducts {
        return ViewHolderVisitsProducts(
            LayoutInflater.from(context).inflate(
                R.layout.listview_visits_products,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolderVisitsProducts, position: Int) {

        var itemProducts = items[position]

        holder?.textviewVisitProduct?.text = itemProducts.productsName
        holder?.textviewProductQty?.text = itemProducts.productsQTy.toString()

    }




inner class ViewHolderVisitsProducts(view: View) : RecyclerView.ViewHolder(view) {
    val textviewVisitProduct = view.textview_visitproduct
    val textviewProductQty = view.textview_product_qty

}
}