package project.emarge.fertilizerrep.views.adaptors.home.complain

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_products.view.*

import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Products


class ComplainProductsAdaptor(val items: ArrayList<Products>, val context: Context) :
    RecyclerView.Adapter<ComplainProductsAdaptor.ViewHolderComplainProductAssignedAdaptor>() {

    lateinit var mClickListener: ClickListener

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderComplainProductAssignedAdaptor {
        return ViewHolderComplainProductAssignedAdaptor(LayoutInflater.from(context).inflate(R.layout.listview_products, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderComplainProductAssignedAdaptor, position: Int) {
        var itemPostion = items[position]
        holder?.textviewProductname?.text = itemPostion.productsName


        if (itemPostion.isProductSelected) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#088946"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"))
        }

    }


    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }
    interface ClickListener {
        fun onClick(products: Products, aView: View)
    }

    inner class ViewHolderComplainProductAssignedAdaptor(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        val textviewProductname = view.textview_product_name
        val cardView = view.card_view


        init {
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            mClickListener.onClick( items[adapterPosition], p0!!)

            for (d in items) {
                d.isProductSelected=false
            }
            items[adapterPosition].isProductSelected=true
            notifyDataSetChanged()
        }
    }
}

