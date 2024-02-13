package com.macruware.fakestore.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :ViewModel() {
    private var searchQuery = MutableLiveData<String>()
    private var lambdaSearch: (() -> Unit)? = null

    // Se configura la lambdaFunction a ejecutar desde el onCreate del fragment
    fun setLambdaFunction(function: (() -> Unit)?){
        lambdaSearch = function
    }

    // Desde MainActivity se guarda el valor del query y se ejecuta la lambdaFunction
    fun setQuery(query: String){
        searchQuery.value = query
        lambdaSearch?.let { it() }
    }

}