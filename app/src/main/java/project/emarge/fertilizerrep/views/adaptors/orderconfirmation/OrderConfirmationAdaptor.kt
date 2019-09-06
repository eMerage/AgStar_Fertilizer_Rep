package project.emarge.fertilizerrep.views.adaptor.visits

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.listview_notconfrim_orders.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Orders
import project.emarge.fertilizerrep.viewModels.orderconfirmations.OrderConfirmationViewModel


class OrderConfirmationAdaptor(val items: ArrayList<Orders>, val context: Context,order : OrderConfirmationViewModel) : RecyclerView.Adapter<OrderConfirmationAdaptor.ViewHolderOrderConfirmation>() {



  val orderViewModel : OrderConfirmationViewModel = order

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOrderConfirmation {
        return ViewHolderOrderConfirmation(LayoutInflater.from(context).inflate(R.layout.listview_notconfrim_orders, parent, false))

    }



    override fun onBindViewHolder(holder: ViewHolderOrderConfirmation, position: Int) {
        var itemPostion = items[position]

        holder?.textviewVisitcode?.text = itemPostion.visitsCode
        holder?.textviewOrdercode?.text = itemPostion.orderCode
        holder?.textviewDealer?.text = itemPostion.dealerName
        holder?.textviewDealerCode?.text = itemPostion.dealerCode
        holder?.textviewDate?.text = itemPostion.orderDate?.substring(0,10)


        holder?.btnConfirm.setOnClickListener {
            orderViewModel.setOrderConfrimation(itemPostion.orderID, holder.editTextConfirmationCode.text.toString())
        }


        holder?.btnResend.setOnClickListener {
            orderViewModel.setReSendConfrimation(itemPostion.orderID)
        }




    }





    inner class ViewHolderOrderConfirmation(view: View) : RecyclerView.ViewHolder(view) {



        val textviewVisitcode = view.textview_notconfrim_orders_visitcode
        val textviewDealer = view.textview_notconfrim_orders_dealer

        val textviewOrdercode = view.textview_notconfrim_orders_ordercode
        val textviewDealerCode = view.textview_notconfrim_orders_dealer_code
        val textviewDate = view.textview_notconfrim_orders_date


        val editTextConfirmationCode = view.editText_dialogorderconfimations_confirmation_code

        val btnConfirm = view.button_dialogorderconfimations_confirm
        val btnResend = view.button_dialogorderconfimations_resend





    }
}

