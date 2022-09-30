package com.app.ancoturf.data.offer

import androidx.fragment.app.Fragment

class ClsQuickLinks {

    var title : String = ""
    var bgImage : Int = 0
    var bgImgUrl : String = ""
    var fragment : Fragment

    constructor(offerName: String, bgImage: Int,bgImgUrl : String, fragment : Fragment) {
        this.title = offerName
        this.bgImage = bgImage
        this.bgImgUrl = bgImgUrl
        this.fragment = fragment
    }
}