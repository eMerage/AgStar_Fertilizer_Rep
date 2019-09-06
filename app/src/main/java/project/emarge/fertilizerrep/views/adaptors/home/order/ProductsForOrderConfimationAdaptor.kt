package project.emarge.fertilizerrep.views.adaptors.home.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_added_products.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Products

class ProductsForOrderConfimationAdaptor(val items: ArrayList<Products>, val context: Context) : RecyclerView.Adapter<ProductsForOrderConfimationAdaptor.ViewHolderAddedProductAssignedAdaptor>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAddedProductAssignedAdaptor {
        return ViewHolderAddedProductAssignedAdaptor(LayoutInflater.from(context).inflate(R.layout.listview_products_order_confim, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderAddedProductAssignedAdaptor, position: Int) {
        var itemPostion = items[position]
        holder?.textviewProductname?.text = itemPostion.productsName
        holder?.textviewProductQty?.text = itemPostion.productsQTy.toString()
    }

    inner class ViewHolderAddedProductAssignedAdaptor(view: View) : RecyclerView.ViewHolder(view) {
        val textviewProductname = view.textview_added_product
        val textviewProductQty = view.textview_added_product_qty

    }
}
