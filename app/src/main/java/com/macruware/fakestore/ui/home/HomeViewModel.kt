package com.macruware.fakestore.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.main.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private var searchQuery = MutableLiveData<String>()
    private var lambdaSearch: (() -> Unit)? = null

    // Se configura la lambdaFunction a ejecutar desde el onCreate del fragment
    // La lambdaFunction es referente a lo que se hace en el search dependiendo del fragment en el que se esté
    // Cada fragment que interactúe con el search, seteará su propia lambdaFunction
    // La lambdaFunction se ejecutará cada que se mande un nuevo query, es decir, una nueva búsqueda
    fun setLambdaFunction(function: (() -> Unit)?){
        lambdaSearch = function
    }

    // Desde MainActivity se guarda el valor del query y se ejecuta la lambdaFunction
    fun setQuery(query: String){
        searchQuery.value = query
        lambdaSearch?.let { it() }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
//    private var _currentProduct = MutableStateFlow<ProductModel?>(null)
//    val currentProduct : StateFlow<ProductModel?> get() = _currentProduct

    private var _currentProduct = MutableLiveData<ProductModel?>(null)
    val currentProduct : LiveData<ProductModel?> get() = _currentProduct

    fun setCurrentProduct(product: ProductModel?){
        _currentProduct.value = product
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private var _mainUiState = MutableStateFlow<MainUiState>(MainUiState.HomeProductList)
    val mainUiState : StateFlow<MainUiState> get() = _mainUiState

    fun setMainUiState(state: MainUiState){
        _mainUiState.value = state
    }


    private var _onBackPressed = MutableLiveData<(() -> Unit)?>(null)
    val onBackPressed : LiveData<(() -> Unit)?> get() = _onBackPressed

    fun setOnBackPressedFunction(function: (() -> Unit)?){
        _onBackPressed.value = function
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}