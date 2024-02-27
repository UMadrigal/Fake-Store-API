package com.macruware.fakestore.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.CategoryNameModel
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.domain.usecase.GetAllCategoriesUseCase
import com.macruware.fakestore.domain.usecase.GetAllProductsUseCase
import com.macruware.fakestore.domain.usecase.GetProductsInCategoryUseCase
import com.macruware.fakestore.ui.home.searchedplp.SearchedApiState
import com.macruware.fakestore.ui.main.MainApiState
import com.macruware.fakestore.ui.main.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getProductsInCategoryUseCase: GetProductsInCategoryUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase
) : ViewModel() {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de búsqueda
    private var searchQuery = MutableLiveData<String>()
    private var lambdaSearch: (() -> Unit)? = null

    // Se configura la lambdaFunction a ejecutar desde el onCreate del fragment
    //*** La lambdaFunction es referente a lo que se hace en el search dependiendo del fragment en el que se esté ***//
    // Cada fragment que interactúe con el search, seteará su propia lambdaFunction
    // La lambdaFunction se ejecutará cada que se mande un nuevo query, es decir, una nueva búsqueda
    fun setLambdaFunction(function: (() -> Unit)?) {
        lambdaSearch = function
    }

    // Desde MainActivity se guarda el valor del query y se ejecuta la lambdaFunction
    fun setQuery(query: String) {
        searchQuery.value = query
        lambdaSearch?.let { it() }
    }
    ////////////////////////////////////////

    private var _productList = MutableStateFlow<List<ProductModel>>(emptyList())
    private var _searchedApiState = MutableStateFlow<SearchedApiState>(SearchedApiState.Loading)
    val searchedApiState: StateFlow<SearchedApiState> get() = _searchedApiState

    fun getAllProducts() {
        _searchedApiState.value = SearchedApiState.Loading

        // llamar api get all products /products
        viewModelScope.launch {
            val response = withContext(IO) { getAllProductsUseCase.invoke() }

            // si no es null
            if (response != null) {
                // Guardar lista de todos los productos
                _productList.value = response

                // Filtrar query en lista de productos
                _searchedApiState.value = SearchedApiState.Success(filterByQuery())
            } else {
                _searchedApiState.value =
                    SearchedApiState.Error("Ha ocurrido un problema al obtener el listado de productos")
            }
        }

    }

    // Función llamada desde searchedFragment a través de lambda
    fun reSearchQuery(): List<ProductModel> {
        // Filtrar query en lista de productos
        return filterByQuery()
    }

    // Función llamada desde categoryFragment a través de lambda
    fun searchIntoCategory() {
        // obtener lista de productos de la categoría
        _productList.value = getProductListOfCurrentCategory()

        // Filtrar query en lista de productos
        _productListFromCategory.value = filterByQuery()
    }

    private fun filterByQuery(): List<ProductModel> {
        return _productList.value.filter {
            it.name.contains(
                searchQuery.value.toString(),
                ignoreCase = true
            )
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de guardado de producto actual
    private var _currentProduct = MutableStateFlow<ProductModel?>(null)
    val currentProduct: StateFlow<ProductModel?> get() = _currentProduct

    fun setCurrentProduct(product: ProductModel?) {
        _currentProduct.value = product
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de estados de la ui de main dependiendo del fragment que se visualice
    private var _mainUiState = MutableStateFlow<MainUiState>(MainUiState.HomeProductList)
    val mainUiState: StateFlow<MainUiState> get() = _mainUiState

    fun setMainUiState(state: MainUiState) {
        _mainUiState.value = state
    }


    private var _onBackPressed = MutableLiveData<(() -> Unit)?>(null)
    val onBackPressed: LiveData<(() -> Unit)?> get() = _onBackPressed

    fun setOnBackPressedFunction(function: (() -> Unit)?) {
        _onBackPressed.value = function
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de estados de la api de main
    // Pide las categorías para mostrar en el menú
    private var _mainApiState = MutableStateFlow<MainApiState>(MainApiState.Loading)
    val mainApiState: StateFlow<MainApiState> get() = _mainApiState

    private var _categoryList = MutableStateFlow<List<CategoryNameModel>>(emptyList())

    fun getCategories() {
        _mainApiState.value = MainApiState.Loading
        // llamar a la api /products/categories
        viewModelScope.launch {
            val response = withContext(IO) {
                getAllCategoriesUseCase.invoke()
            }

            // si no es null
            if (response != null) {
                // Guardamos la lista de las categorías
                _categoryList.value = response

                // Cambiamos el estado de mainApi y le mandamos la lista
                _mainApiState.value = MainApiState.Success(_categoryList.value)

                // Obtener todos los productos de cada categoría
                getAllProductsInCategory()
            } else {
                _mainApiState.value =
                    MainApiState.Error("No ha sido posible obtener la información del servidor.")
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de estados de la api de home
    // Pide la lista de productos de cada categoría para homePlp
    private var _homeApiState = MutableStateFlow<HomeApiState>(HomeApiState.Loading)
    val homeApiState: StateFlow<HomeApiState> get() = _homeApiState

    private var _categoryWithProductList = MutableStateFlow(mutableListOf<CategoryProductModel>())

    private fun getAllProductsInCategory() {
        _homeApiState.value = HomeApiState.Loading
        _categoryWithProductList.value = mutableListOf()

            viewModelScope.launch {
                // llamar a la api por cada una de las categorías
                val deferredResponses = _categoryList.value.map { category ->
                    async(IO) {
                        getProductsInCategoryUseCase.invoke(category.name)
                    }
                }

                // obtener el listado de respuestas en el mismo orden que se mandaron
                val responses = deferredResponses.map { deferred -> deferred.await() }

                // iterar la lista de respuestas y si no es null, guardar en lista de categoryWithProduct
                for ((index, response) in responses.withIndex()) {
                    if (response != null) {
                        _categoryWithProductList.value.add(response)
                    } else {
                        _homeApiState.value =
                            HomeApiState.Error("Error al obtener lista de productos de la categoría: ${_categoryList.value[index].name}")
                        return@launch
                    }
                }

                // una vez guardadas todas las responses, actualizar el state de homeApi
                val updatedList: List<CategoryProductModel> = _categoryWithProductList.value
                _homeApiState.value = HomeApiState.Loading
                _homeApiState.value = HomeApiState.Success(updatedList)
            }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque que guarda la categoría seleccionada para ver todos los productos
    private var _currentCategory = MutableStateFlow("")

    private var _productListFromCategory = MutableStateFlow<List<ProductModel>>(emptyList())
    val productListFromCategory: StateFlow<List<ProductModel>> get() = _productListFromCategory

    fun setCurrentCategory(newCategory: String) {
        _currentCategory.value = newCategory
        _productListFromCategory.value = getProductListOfCurrentCategory()
    }

    // Al cambiar a CategoryPlp se guarda la lista de productos de la categoría seleccionada
    private fun getProductListOfCurrentCategory(): List<ProductModel> {
        for (categoryWithProduct in _categoryWithProductList.value) {
            if (categoryWithProduct.category == _currentCategory.value) {
                return categoryWithProduct.productList
            }
        }
        return emptyList()
    }

    private var _lambdaFunctionForCategory: ((String) -> Unit)? = null

    fun setLambdaFunctionForCategory(category: (String) -> Unit) {
        _lambdaFunctionForCategory = category
    }

    fun goToCategory(category: String) {
        _lambdaFunctionForCategory?.let { it(category) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private var _cartProductList = MutableStateFlow<List<CartProductModel>>(
        mutableListOf(
            CartProductModel(2, ProductModel("Electronic Product 1", "49.99", "jewerly","Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10)),
            CartProductModel(1, ProductModel("Electronic Product 2", "99.99", "jewerly", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8)),
            CartProductModel(3, ProductModel("Electronic Product 3", "29.99", "jewerly", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15)),
            CartProductModel(1, ProductModel("Electronic Product 4", "79.99", "jewerly", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12)),
            CartProductModel(2, ProductModel("Electronic Product 5", "59.99", "jewerly", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20))
        )
    )
    val cartProductList: StateFlow<List<CartProductModel>> get() = _cartProductList

    fun addProductToCart(cartProduct: CartProductModel) {
        val newList : MutableList<CartProductModel> = cartProductList.value.map { it.copy() }.toMutableList()
        var isContent = false
        for (cartItem in newList){
            if (cartItem.product.name == cartProduct.product.name){
                isContent = true
                val index = newList.indexOf(cartItem)
                newList[index].quantity++
            }
        }
        if (!isContent){
            newList.add(cartProduct)
        }

        setCartProductList(newList)
    }

    fun removeProductFromCart(cartProduct: CartProductModel) {
        val newList : MutableList<CartProductModel> = cartProductList.value.map { it.copy() }.toMutableList()

        for (cartItem in newList){
            if (cartItem.product.name == cartProduct.product.name){

                val index = newList.indexOf(cartItem)
                if (newList[index].quantity > 1){
                    newList[index].quantity--
                } else {
                    newList.removeAt(index)
                }

                setCartProductList(newList)
                break
            }
        }
    }

    private fun setCartProductList(list: List<CartProductModel>){
        _cartProductList.value = list
        // Guardar en firebase
    }

    fun getCartProductList(){
        // Llamar a firebase
        val cartProductList = _cartProductList.value
        setCartProductList(cartProductList)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}