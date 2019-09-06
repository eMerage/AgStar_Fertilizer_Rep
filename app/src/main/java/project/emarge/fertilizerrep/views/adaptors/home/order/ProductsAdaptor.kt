package project.emarge.fertilizerrep.views.adaptors.home.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_products.view.*

import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Products


class ProductsAdaptor(val items: ArrayList<Products>, val context: Context) :
    RecyclerView.Adapter<ProductsAdaptor.ViewHolderProductAssignedAdaptor>() {

    lateinit var mClickListener: ClickListener

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderProductAssignedAdaptor {
        return ViewHolderProductAssignedAdaptor(LayoutInflater.from(context).inflate(R.layout.listview_products, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderProductAssignedAdaptor, position: Int) {
        var itemPostion = items[position]
        holder?.textviewProductname?.text = itemPostion.productsName
    }


    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }
    interface ClickListener {
        fun onClick(products: Products, aView: View)
    }

    inner class ViewHolderProductAssignedAdaptor(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        val textviewProductname = view.textview_product_name
        init {
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            mClickListener.onClick( items[adapterPosition], p0!!)
        }
    }
}

