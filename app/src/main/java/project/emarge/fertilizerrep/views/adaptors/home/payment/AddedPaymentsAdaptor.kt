package project.emarge.fertilizerrep.views.adaptors.home.payment

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.listview_added_payments.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Payment

class AddedPaymentsAdaptor(val items: ArrayList<Payment>, val context: Context) : RecyclerView.Adapter<AddedPaymentsAdaptor.ViewHolderAddedPayments>() {

    lateinit var mClickListener: ClickListener

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAddedPayments {
        return ViewHolderAddedPayments(LayoutInflater.from(context).inflate(R.layout.listview_added_payments, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderAddedPayments, position: Int) {
        var itemPostion = items[position]

        holder.textviewOrdernumber.text = itemPostion.orderNumber
        holder.textviewValue.text = itemPostion.paymentValue.toString()
        holder.textviewPaymenttype.text = itemPostion.paymentType


        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.ic_camera_48)
        requestOptions.error(R.drawable.ic_camera_48)

        val requestListener = object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Bitmap>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
            override fun onResourceReady(
                resource: Bitmap,
                model: Any,
                target: Target<Bitmap>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(itemPostion.isImageFromCamera){
                Glide.with(context)
                    .asBitmap()
                    .load(itemPostion.paymentImage.path)
                    .apply(requestOptions)
                    .listener(requestListener)
                    .into(holder.imageViewCover)
            }else{
                Glide.with(context)
                    .asBitmap()
                    .load(itemPostion.paymentImage)
                    .apply(requestOptions)
                    .listener(requestListener)
                    .into(holder.imageViewCover)
            }
        }else{
            Glide.with(context)
                .asBitmap()
                .load(itemPostion.paymentImage)
                .apply(requestOptions)
                .listener(requestListener)
                .into(holder.imageViewCover)
        }

    }


    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }
    interface ClickListener {
        fun onClick(payment: Payment, aView: View)
    }

    inner class ViewHolderAddedPayments(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textviewOrdernumber = view.textview_paymentlist_ordernumber
        val textviewValue = view.textview_paymentlist_value
        val textviewPaymenttype = view.textview_paymentlist_paymenttype
        val imageViewCover = view.img_paymentlist_image

        init {
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            mClickListener.onClick( items[adapterPosition], p0!!)
        }
    }
}
