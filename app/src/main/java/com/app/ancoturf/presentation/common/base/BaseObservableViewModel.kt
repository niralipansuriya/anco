package com.app.ancoturf.presentation.common.base

import android.app.Application
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.Serializable

/**
 * A ViewModel that is also an Observable,
 * to be used with the Data Binding Library.
 */
open class BaseObservableViewModel(app: Application) : AndroidViewModel(app), Observable {

    private val compositeDisposable = CompositeDisposable()
    protected fun Disposable.collect() = compositeDisposable.add(this)

    val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String?> get() = _errorLiveData

    val _isNextPageUrl = MutableLiveData<Boolean>()
    val _isNextPageUrlForProducts = MutableLiveData<Boolean>()

    val _maintanaceErrorLiveData = MutableLiveData<Boolean>()
    val maintanaceErrorLiveData: LiveData<Boolean> get() = _maintanaceErrorLiveData

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
        callbacks.clear()
    }

    private val callbacks: PropertyChangeRegistry = PropertyChangeRegistry()

    override fun addOnPropertyChangedCallback(
            callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.add(callback)
    }

    override fun removeOnPropertyChangedCallback(
            callback: Observable.OnPropertyChangedCallback
    ) {
        callbacks.remove(callback)
    }

    /**
     * Notifies observers that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies observers that a specific property has changed. The getter for the
     * property that changes should be marked with the @Bindable annotation to
     * generate a field in the BR class to be used as the fieldId parameter.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }
}