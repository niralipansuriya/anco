package com.app.ancoturf.presentation.signup

import android.app.Application
import androidx.databinding.Bindable
import com.app.ancoturf.BR
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import javax.inject.Inject

open class SignupvariableViewModel @Inject constructor(
    val app: Application
) : BaseObservableViewModel(app) {

    @Bindable
    var landscaper: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.landscaper)
        }

    @Bindable
    var firstName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }

    @Bindable
    var lastName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }

    @Bindable
    var email: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }

    @Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    @Bindable
    var businessName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.businessName)
        }

    @Bindable
    var abn: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.abn)
        }

    @Bindable
    var phoneNumber: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phoneNumber)
        }
}