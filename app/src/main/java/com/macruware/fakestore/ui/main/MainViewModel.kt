package com.macruware.fakestore.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.CategoryNameModel
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.FavoriteProductModel
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.domain.usecase.GetAllCategoriesUseCase
import com.macruware.fakestore.domain.usecase.GetAllProductsUseCase
import com.macruware.fakestore.domain.usecase.GetProductsInCategoryUseCase
import com.macruware.fakestore.ui.home.HomeApiState
import com.macruware.fakestore.ui.home.searchedplp.SearchedApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
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

    private var isInit = true

    fun getAllProducts() {
        if (isInit){
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

                    isInit = false
                } else {
                    _searchedApiState.value =
                        SearchedApiState.Error("Ha ocurrido un problema al obtener el listado de productos")
                }
            }
        } else {
            _searchedApiState.value = SearchedApiState.Success(reSearchQuery())
        }

    }

    // Función llamada desde searchedFragment a través de lambda
    private fun reSearchQuery(): List<ProductModel> {
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

    private var _goBackToHome = MutableStateFlow(false)
    val goBackToHome : StateFlow<Boolean> get() = _goBackToHome

    fun setGoBackToHome(boolean: Boolean){
        _goBackToHome.value = boolean
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
    //////////// Cart ///////////
    private var _cartProductList = MutableStateFlow<List<CartProductModel>>(emptyList())
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


    private var _isCartProductDeleted = MutableStateFlow(false)
    val isCartProductDeleted : StateFlow<Boolean> get() = _isCartProductDeleted
    private var deletedCartProduct : CartProductModel? = null
    private var deletedCartProductIndex : Int? = null

    fun removeProductFromCart(cartProduct: CartProductModel) {
        val newList : MutableList<CartProductModel> = cartProductList.value.map { it.copy() }.toMutableList()
        resetDeletedCartProduct()
        for (cartItem in newList){
            if (cartItem.product.name == cartProduct.product.name){

                val index = newList.indexOf(cartItem)
                if (newList[index].quantity > 1){
                    newList[index].quantity--
                } else {
                    deletedCartProduct = newList[index]
                    deletedCartProductIndex = index
                    newList.removeAt(index)
                    _isCartProductDeleted.value = true
                }

                setCartProductList(newList)
                break
            }
        }
    }

    fun undoRemoveProductFromCart(){
        if (isCartProductDeleted.value){
            if (deletedCartProduct != null && deletedCartProductIndex != null){
                val newList : MutableList<CartProductModel> = cartProductList.value.map { it.copy() }.toMutableList()
                newList.add(deletedCartProductIndex!!, deletedCartProduct!!)
                resetDeletedCartProduct()
                setCartProductList(newList)
            }
        }
    }

    fun resetDeletedCartProduct(){
        deletedCartProduct = null
        deletedCartProductIndex = null
        _isCartProductDeleted.value = false
    }

    private var _isEmptyCart = MutableStateFlow(true)
    val isEmptyCart : StateFlow<Boolean> get() = _isEmptyCart

    private fun setCartProductList(list: List<CartProductModel>){
//        _isEmptyCart.value = _cartProductList.value.isEmpty()
        _isEmptyCart.value = list.isEmpty()
        _cartProductList.value = list
        // Guardar en firebase
    }

    fun getCartProductList(){
        // Llamar a firebase
        _mainUiState.value = MainUiState.CartDetailFragment
        val cartProductList = _cartProductList.value
        setCartProductList(cartProductList)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
//////////// Favorites ///////////
    private var _favoriteProductList = MutableStateFlow<List<FavoriteProductModel>>(emptyList())
    val favoriteProductList: StateFlow<List<FavoriteProductModel>> get() = _favoriteProductList

    private var _isCurrentProductInFavorites = MutableStateFlow(false)
    val isCurrentProductInFavorites : StateFlow<Boolean> get() = _isCurrentProductInFavorites

    private fun setCurrentProductInFavorites(boolean: Boolean){
        _isCurrentProductInFavorites.value = boolean
    }

    fun addProductToFavorites(favoriteProduct: FavoriteProductModel) {
        val newList : MutableList<FavoriteProductModel> = favoriteProductList.value.map { it.copy() }.toMutableList()

        if (!newList.contains(favoriteProduct)){
            newList.add(favoriteProduct)

            if (currentProduct.value != null){
                if(favoriteProduct.product.name == currentProduct.value!!.name){
                    _isCurrentProductInFavorites.value = true
                }
            } else {
                _isCurrentProductInFavorites.value = false
            }

            setFavoriteProductList(newList)
        }
    }

    fun isProductInFavorites(): Boolean{
        var isInFavorites = false
        if (favoriteProductList.value.isNotEmpty() && currentProduct.value != null){
            for (favoriteItem in favoriteProductList.value){
                if(favoriteItem.product.name == currentProduct.value!!.name){
                    isInFavorites = true
                }
            }
        }
        setCurrentProductInFavorites(isInFavorites)
        return isInFavorites
    }

    private var _isFavoriteProductDeleted = MutableStateFlow(false)
    val isFavoriteProductDeleted : StateFlow<Boolean> get() = _isFavoriteProductDeleted
    private var deletedFavoriteProduct : FavoriteProductModel? = null
    private var deletedFavoriteProductIndex : Int? = null

    fun removeProductFromFavorites(favoriteProduct: FavoriteProductModel) {
        val newList : MutableList<FavoriteProductModel> = favoriteProductList.value.map { it.copy() }.toMutableList()
        resetDeletedFavoriteProduct()
        for (favoriteItem in favoriteProductList.value){
            if (favoriteItem.product.name == favoriteProduct.product.name){
                val index = newList.indexOf(favoriteItem)
                deletedFavoriteProduct = newList[index]
                deletedFavoriteProductIndex = index
                newList.removeAt(index)
                _isFavoriteProductDeleted.value = true

                if (currentProduct.value != null){
                    if(favoriteProduct.product.name == currentProduct.value!!.name){
                        _isCurrentProductInFavorites.value = false
                    }
                } else {
                    _isCurrentProductInFavorites.value = false
                }

                setFavoriteProductList(newList)
            }
        }
    }

    fun undoRemoveProductFromFavorites(){
        if (isFavoriteProductDeleted.value){
            if (deletedFavoriteProduct != null && deletedFavoriteProductIndex != null){
                val newList : MutableList<FavoriteProductModel> = favoriteProductList.value.map { it.copy() }.toMutableList()
                newList.add(deletedFavoriteProductIndex!!, deletedFavoriteProduct!!)
                resetDeletedFavoriteProduct()
                setFavoriteProductList(newList)
            }
        }
    }

    fun resetDeletedFavoriteProduct(){
        deletedFavoriteProduct = null
        deletedFavoriteProductIndex = null
        _isFavoriteProductDeleted.value = false
    }

    private var _isEmptyFavorites = MutableStateFlow(true)
    val isEmptyFavorites : StateFlow<Boolean> get() = _isEmptyFavorites

    private fun setFavoriteProductList(list: List<FavoriteProductModel>){
        _isEmptyFavorites.value = list.isEmpty()
        _favoriteProductList.value = list
        // Guardar en firebase
    }

    fun getFavoritesProductList(){
        // Llamar a firebase
        _mainUiState.value = MainUiState.FavoritesListFragment
        val favoriteProductList = _favoriteProductList.value
        setFavoriteProductList(favoriteProductList)
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}