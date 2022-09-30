package com.app.ancoturf.presentation.chooseingMyLawn

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.extension.showAlert
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Logger
import kotlinx.android.synthetic.main.fragment_choose_my_lawn_question.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

/*https://stackoverflow.com/questions/19602369/how-to-disable-viewpager-from-swiping-in-one-direction/34076649*/
/**
 * A simple [Fragment] subclass.
 * Use the [ChooseMyLawnQuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseMyLawnQuestionFragment : BaseFragment(), View.OnClickListener {
    private var currentQueNo: Int = 0
    private var queAnsList: ArrayList<QuestionAnswerData>? = ArrayList()
    private var setQueAnsData: QuestionAnswerData = QuestionAnswerData()
    var viewArr = arrayOfNulls<View>(5)

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var compContent: ArrayList<String> = ArrayList()
    var finalContent: ArrayList<String> = ArrayList()
    var isSetAnswer1stTime: Boolean = false
    var finalSelAns : String = ""
    private lateinit var dialog: Dialog
    override fun getContentResource(): Int = R.layout.fragment_choose_my_lawn_question

    override fun viewModelSetup() {
    }

    override fun viewSetup() {
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, true)
        }
        setQuestionAnswer()
        currentQueNo = 1
        setCurrentQueData(currentQueNo)
        eventListener()

        viewArr[0] = lineQue1
        viewArr[1] = lineQue2
        viewArr[2] = lineQue3
        viewArr[3] = lineQue4
        viewArr[4] = lineQue5
    }

    private fun openInfoDialog(){
        // Create custom dialog object
//        dialog = Dialog(requireContext())
        val dialog = Dialog(requireContext() , R.style.CustomAlertDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_product_info)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)

//        val txtTitle = dialog.findViewById(R.id.txtTitle) as AppCompatTextView
        val txtDescription = dialog.findViewById(R.id.txtDescription) as AppCompatTextView
        txtDescription.text = queAnsList?.get(currentQueNo - 1)?.infoDescription
        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun eventListener() {
        btnNo.setOnClickListener(this)
        btnYes.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        ivInfo.setOnClickListener(this)

        /*csQuestion?.onTouchEvent(object : OnSwipeTouchListener{

        })*/

        csQuestion.setOnTouchListener(object : OnSwipeTouchListener(requireContext()){
            override fun onSwipeRight() {
                super.onSwipeRight()
                Logger.log("dwi -> right")
                if (currentQueNo != 1){
                    setPreQuestion()
                }
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Logger.log("dwi -> left")
            }
        })
    }

    private fun setCurrentQueData(curQueNo: Int) {
        for (i in 0 until queAnsList?.size!!) {
            if (curQueNo == i + 1)
                setQueAnsData = queAnsList?.get(i)!!
        }
        ivGuide?.setImageResource(setQueAnsData.queImage)
        tvQuestion.text = setQueAnsData.queTitle
        tvDescription.text = setQueAnsData.queDescription
        btnYes.text = setQueAnsData.queOption1
        btnNo.text = setQueAnsData.queOption2
//        ivInfo.visibility = if (currentQueNo == 1) View.VISIBLE else View.GONE

        for (i in 0 until viewArr?.size) {
            if (i + 1 <= curQueNo) {
                viewArr[i]?.setBackgroundResource(R.drawable.line_question_selected)
            } else {
                viewArr[i]?.setBackgroundResource(R.drawable.line_que_deselected)
            }
        }
    }

    private fun setQuestionAnswer() {
        queAnsList?.clear()

        //add 1st queDetail
        var data1ist1: QuestionAnswerData = QuestionAnswerData()
        data1ist1.queNo = 1
        data1ist1.queImage = R.drawable.ic_step_by_step_1
        data1ist1.queTitle = getString(R.string.is_the_lawn_a_full_sun_Area)
        data1ist1.queDescription = getString(R.string.that_means_your_lawn_is_getting)
        data1ist1.queOption1 = getString(R.string.yes)
        data1ist1.queOption2 = getString(R.string.no)
//        data1ist1.queContent = "S/W,T/F,KIK,RTF"
        data1ist1.queContent = "S/W,T/T,KIK,RTF"
        data1ist1.infoDescription = getString(R.string.full_Sun_means_direct_unfiltered_sunlight)

        //add 2nd queDetail
        var data1ist2: QuestionAnswerData = QuestionAnswerData()
        data1ist2.queNo = 2
        data1ist2.queImage = R.drawable.ic_step_by_step_2
        data1ist2.queTitle = getString(R.string.is_the_lawn_a_sun_shade_area)
        data1ist2.queDescription = getString(R.string.sun_shade_area_means_there_is_both)
        data1ist2.queOption1 = getString(R.string.yes)
        data1ist2.queOption2 = getString(R.string.no)
        data1ist2.queContent = "S/W,RTF"
        data1ist2.infoDescription = getString(R.string.sun_shade_means_your_lawn_gets)


        //add 3rd queDetail
        var data1ist3: QuestionAnswerData = QuestionAnswerData()
        data1ist3.queNo = 3
        data1ist3.queImage = R.drawable.ic_step_by_step_3
        data1ist3.queTitle = getString(R.string.is_the_lawn_a_high_traffic)
        data1ist3.queDescription = getString(R.string.are_Active_dogs_kids_vehicle)
        data1ist3.queOption1 = getString(R.string.yes)
        data1ist3.queOption2 = getString(R.string.no)
//        data1ist3.queContent = "KIK,T/F"
        data1ist3.queContent = "KIK,T/T"
        data1ist3.infoDescription = getString(R.string.high_traffic_lawns_are_those)

        //add 4th queDetail
        var data1ist4: QuestionAnswerData = QuestionAnswerData()
        data1ist4.queNo = 4
        data1ist4.queImage = R.drawable.ic_broadleaf_fine_leaf
        data1ist4.queTitle = getString(R.string.do_you_like_a_broadleaf_lawn)
        data1ist4.queDescription = getString(R.string.broad_leaf_lawn_mean_that_the_grass)
        data1ist4.queOption1 = getString(R.string.broadleaf)
        data1ist4.queOption2 = getString(R.string.fine_leaf)
//        data1ist4.queContent = "S/W,KIK"
        //nnn here
        data1ist4.queContent = "S/W"
        data1ist4.queContentOfNo = "RTF,KIK,T/T,SG"
//        data1ist4.queContentOfNo = "RTF,KIK,T/T,S/G"
        data1ist4.infoDescription = getString(R.string.broadleaf_grass_refers_to_a)

        //add 5th queDetail
        var data1ist5: QuestionAnswerData = QuestionAnswerData()
        data1ist5.queNo = 5
        data1ist5.queImage = R.drawable.ic_step_by_step_13
        data1ist5.queTitle = getString(R.string.is_the_lawn_for_sale)
        data1ist5.queDescription =
            getString(R.string.are_you_redoing_the_lawn_for_the_purpose_selling)
        data1ist5.queOption1 = getString(R.string.yes)
        data1ist5.queContent = "RTF,S/W"
        data1ist5.queOption2 = getString(R.string.no)
        data1ist5.infoDescription = getString(R.string.if_you_are_Selling_your_home)

        queAnsList?.add(data1ist1)
        queAnsList?.add(data1ist2)
        queAnsList?.add(data1ist3)
        queAnsList?.add(data1ist4)
        queAnsList?.add(data1ist5)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnYes -> {
//                setAnswerAndCompare()
                setNextQuestion(true)
            }
            R.id.btnNo -> {
                setNextQuestion(false)
            }
            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }
            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )

            }
            R.id.imgCart -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (sharedPrefs.totalProductsInCart > 0) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        CartFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    shortToast(getString(R.string.no_product_in_cart_message))
                }
            }
            R.id.ivInfo ->{
                openInfoDialog()
            }
        }
    }

    private fun setAllAnswerAndCompare() {
        for (i in 0 until queAnsList?.size!!) {
            if (queAnsList?.get(i)?.queAnswer!! || i == 3){

                if (queAnsList?.get(i)?.queAnswer!!){
                    compContent = queAnsList?.get(i)?.queContent?.split(",")
                        ?.map { it.trim() } as ArrayList<String>
                }else{
                    compContent = queAnsList?.get(i)?.queContentOfNo?.split(",")
                        ?.map { it.trim() } as ArrayList<String>
                }

                if (!isSetAnswer1stTime || finalContent.isEmpty()) {
                    finalContent.addAll(compContent)
                }
                finalContent.retainAll(compContent)
                if (!isSetAnswer1stTime) {
                    isSetAnswer1stTime = true
                }
                finalSelAns = finalContent.joinToString(separator = ",")
            }
        }
    }
    private fun setAnswerAndCompare() {

        compContent = queAnsList?.get(currentQueNo - 1)?.queContent?.split(",")
            ?.map { it.trim() } as ArrayList<String>
        if (!isSetAnswer1stTime || finalContent.isEmpty()) {
            finalContent.addAll(compContent)
        }
        finalContent.retainAll(compContent)
        if (!isSetAnswer1stTime) {
            isSetAnswer1stTime = true
        }
        finalSelAns = finalContent.joinToString(separator = ",")
    }

    private fun setNextQuestion(isYesNo : Boolean) {
        if (currentQueNo != 5) {
            queAnsList?.get(currentQueNo-1)?.queAnswer = isYesNo
            currentQueNo += 1
            setCurrentQueData(currentQueNo)
        } else {
            setAllAnswerAndCompare()
            openRecommendedFragment()
        }
    } private fun setPreQuestion() {
        if (currentQueNo != 1) {
            currentQueNo -= 1
            setCurrentQueData(currentQueNo)
        }
    }

    private fun openRecommendedFragment() {
        val bundle = Bundle()
        if (finalSelAns == ""){
            finalSelAns = "S/W"
        }
        bundle.putString("recommendProduct" , finalSelAns)


        if (isOnline()) {
                (requireActivity() as AppCompatActivity).pushFragment(
                    RecommendedProductFragment().apply {
                        arguments = bundle
                    },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }else{
                (requireActivity() as AppCompatActivity).showAlert(getString(R.string.error_no_network))
            }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    public fun isNetworkConnected(): Boolean {
        //1
        val connectivityManager = (requireActivity() as AppCompatActivity).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun isOnline(): Boolean {
        try {
            val cm = (requireActivity() as AppCompatActivity).getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            if (netInfo != null && netInfo.isConnectedOrConnecting) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


}

