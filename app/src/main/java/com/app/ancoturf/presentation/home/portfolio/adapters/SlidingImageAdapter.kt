package com.app.ancoturf.presentation.home.portfolio.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.presentation.home.portfolio.ViewPortfolioFragment

class SlidingImageAdapter(
    var context: Context, var fragmentManager: FragmentManager, var portfolioId: Int
) : FragmentStatePagerAdapter(fragmentManager) {

    var images: ArrayList<PortfolioDetailResponse.PortfolioImage>? = ArrayList()


    override fun getItem(position: Int): Fragment {
        /*return if (position == images?.lastIndex) {
            ViewPortfolioFragment(null, portfolioId)
        } else {
            ViewPortfolioFragment(images?.get(position), portfolioId)
        }*/
        return ViewPortfolioFragment(images?.get(position), portfolioId)

    }

    override fun getCount(): Int {
        return images?.size!!
    }

    fun updateData(data: ArrayList<PortfolioDetailResponse.PortfolioImage>) {

        images?.clear()
        images?.addAll(data)
//        images?.add(PortfolioDetailResponse.PortfolioImage())
        notifyDataSetChanged()

    }
    fun clearViewPagerData(){
        var fragmentList = fragmentManager.fragments
        if (fragmentList?.size > 0) {
            var ft = fragmentManager.beginTransaction()
            for (fragment: Fragment in fragmentList) {
                if (fragment is ViewPortfolioFragment) {
                    ft.remove(fragment)
                }
            }
            ft.commitAllowingStateLoss()
        }
    }
}