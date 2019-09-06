package project.emarge.fertilizerrep.views.adaptors.home

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import project.emarge.fertilizerrep.R
import project.emarge.fertilizerrep.views.fragments.ComplaintFragment
import project.emarge.fertilizerrep.views.fragments.OrderFragment
import project.emarge.fertilizerrep.views.fragments.PaymentFragment
import project.emarge.fertilizerrep.views.fragments.StockFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class PagerHomeAdapter(private val mContext: Context, fm: FragmentManager,visitID : Int,visitcCode : String,dNumber : String) : FragmentPagerAdapter(fm) {


    var visitid = visitID
    var visitcode = visitcCode
    var dealerNumber = dNumber


    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            OrderFragment.newInstance(position + 1,visitid,visitcode,dealerNumber)
        } else if (position == 1) {
            PaymentFragment.newInstance(position + 1,visitid,visitcode)
        } else if(position == 2){
            StockFragment.newInstance(position + 1,visitid,visitcode)
        }else{
            ComplaintFragment.newInstance(position + 1,visitid,visitcode)
        }


    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 4
    }

    companion object {

        @StringRes
        private val TAB_TITLES = intArrayOf( R.string.home_tab_1,
            R.string.home_tab_2,
            R.string.home_tab_3,R.string.home_tab_4)
    }
}