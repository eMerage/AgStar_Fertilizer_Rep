package project.emarge.fertilizerrep.views.adaptors.home.complain

import android.content.Context
import android.graphics.Bitmap
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
import kotlinx.android.synthetic.main.listview_added_compalins.view.*
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.models.datamodel.Complain

class AddedComplainAdaptor(val items: ArrayList<Complain>, val context: Context) : RecyclerView.Adapter<AddedComplainAdaptor.ViewHolderAddedPayments>() {

    lateinit var mClickListener: ClickListener

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAddedPayments {
        return ViewHolderAddedPayments(LayoutInflater.from(context).inflate(R.layout.listview_added_compalins, parent, false))

    }

    override fun onBindViewHolder(holder: ViewHolderAddedPayments, position: Int) {
        var itemPostion = items[position]

        holder.textviewproCoder.text = itemPostion.complainProductsName
        holder.textviewDes.text = itemPostion.complainDes



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


        Glide.with(context)
            .asBitmap()
            .load(itemPostion.complainImagePath)
            .apply(requestOptions)
            .listener(requestListener)
            .into(holder.imageViewCover)


    }


    fun setOnItemClickListener(aClickListener: ClickListener) {
        mClickListener = aClickListener
    }
    interface ClickListener {
        fun onClick(complain: Complain, aView: View)
    }

    inner class ViewHolderAddedPayments(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textviewproCoder = view.textview_complainlist_productcode
        val textviewDes = view.textview_complainlist_des
        val imageViewCover = view.img_complainlist_image

        init {
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            mClickListener.onClick( items[adapterPosition], p0!!)
        }
    }
}
