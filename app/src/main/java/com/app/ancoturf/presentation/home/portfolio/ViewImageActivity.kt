package com.app.ancoturf.presentation.home.portfolio

import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.app.ancoturf.R
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.portfolio.adapters.ViewImageAdapter
import kotlinx.android.synthetic.main.activity_view_image.*
import javax.inject.Inject

class ViewImageActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        const val IMAGES: String = "images"
        const val POSITION: String = "position"
    }

    override fun getContentResource(): Int {
        return R.layout.activity_view_image
    }

    override fun viewModelSetup() {

    }

    override fun viewSetup() {
        window!!.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if (intent.hasExtra(IMAGES)) {
            var images =
                intent.getSerializableExtra(IMAGES) as ArrayList<PortfolioDetailResponse.PortfolioImage>
            var viewImageAdapter = ViewImageAdapter(images)
            vpImages.adapter = viewImageAdapter
            vpImages.currentItem = intent.extras!!.getInt(POSITION)
        }

        imgClose.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgClose -> {
                finish()
            }
        }
    }
}