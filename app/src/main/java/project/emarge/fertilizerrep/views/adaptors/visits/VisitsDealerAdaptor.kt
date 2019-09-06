package project.emarge.fertilizerrep.views.adaptors.visits

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_visits_dealers.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Dealer


class VisitsDealerAdaptor(val items: ArrayList<Dealer>, val context: Context) :
    RecyclerView.Adapter<VisitsDealerAdaptor.ViewHolderVisitsDealerAdaptor>() {

    lateinit var mClickListener: ClickListener


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderVisitsDealerAdaptor {
        return ViewHolderVisitsDealerAdaptor(LayoutInflater.from(context).inflate(R.layout.listview_visits_dealers, parent, false))

    }

    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }
    interface ClickListener {
        fun onClick(dealer: Dealer, aView: View)
    }

    override fun onBindViewHolder(holder: ViewHolderVisitsDealerAdaptor, position: Int) {
        var itemPostion = items[position]
        holder?.textviewName?.text = itemPostion.dealerName
        holder?.textviewCode?.text = itemPostion.dealerCode

        if (itemPostion.isDealerSelected) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#088946"))
        } else {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#ffffff"))
        }

    }

    inner class ViewHolderVisitsDealerAdaptor(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener  {

        val textviewName= view.textview_name
        val textviewCode= view.textview_delar_code


        val cardView = view.card_view_dealer_to_visits

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            mClickListener.onClick( items[adapterPosition], p0!!)

            for (d in items) {
                d.isDealerSelected=false
            }
            items[adapterPosition].isDealerSelected=true
            notifyDataSetChanged()
        }
    }
}

