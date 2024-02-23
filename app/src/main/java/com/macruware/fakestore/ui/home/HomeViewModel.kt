package com.macruware.fakestore.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.macruware.fakestore.domain.model.CategoryNameModel
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.main.MainApiState
import com.macruware.fakestore.ui.main.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

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

    fun getAllProducts(): List<ProductModel>{
        // llamar api get all products /products
        // si no es null
        val response = listOf<ProductModel>(
            ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
            ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
            ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
            ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
            ProductModel("Electronic Product 5", 59.99,"Jewelry", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20),
            ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
            ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
            ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
            ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
            ProductModel("Electronic Product 5", 59.99,"Jewelry", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20),
            ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
            ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
            ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
            ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
            ProductModel("Electronic Product 5", 59.99,"Jewelry", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20),
            ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
            ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
            ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
            ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
            ProductModel("Electronic Product 5", 59.99,"Jewelry", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20)
        )

        // Guardar lista de todos los productos
        _productList.value = response

        // Filtrar query en lista de productos
        return filterByQuery()
    }

    // Función llamada desde searchedFragment a través de lambda
    fun reSearchQuery(): List<ProductModel> {
        // Filtrar query en lista de productos
        return filterByQuery()
    }

    // Función llamada desde categoryFragment a través de lambda
    fun searchIntoCategory(){
        // obtener lista de productos de la categoría
        _productList.value = getProductListOfCurrentCategory()

        // Filtrar query en lista de productos
        _productListFromCategory.value = filterByQuery()
    }

    private fun filterByQuery(): List<ProductModel>{
        return _productList.value.filter { it.name.contains(searchQuery.value.toString(), ignoreCase = true) }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de guardado de producto actual
    private var _currentProduct = MutableStateFlow<ProductModel?>(null)
    val currentProduct : StateFlow<ProductModel?> get() = _currentProduct

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
        // llamar a la api /products/categories

        // si no es null
        val response = listOf(
            CategoryNameModel("electronics"),
            CategoryNameModel("jewelery"),
            CategoryNameModel("men's clothing"),
            CategoryNameModel("women's clothing")
        )
        _categoryList.value = response

        _mainApiState.value = MainApiState.Success(_categoryList.value)

        // Obtener todos los productos de cada categoría
        getAllProductsInCategory()
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque de estados de la api de home
    // Pide la lista de productos de cada categoría para homeplp
    private var _homeApiState = MutableStateFlow<HomeApiState>(HomeApiState.Loading)
    val homeApiState: StateFlow<HomeApiState> get() = _homeApiState

    private var _categoryWithProductList = MutableStateFlow(mutableListOf<CategoryProductModel>())

    private fun getAllProductsInCategory() {
        for (category in _categoryList.value){
            // llamar a la api /products/category/{category}

            // si no es null
            val response = listOf(
                ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
                ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
                ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
                ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
                ProductModel("Electronic Product 5", 59.99,"Jewelry", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20),
                ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
                ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
                ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
                ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
                ProductModel("Electronic Product 5", 59.99,"Jewelry", "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20),
                ProductModel("Electronic Product 1", 49.99,"Jewelry", "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
                ProductModel("Electronic Product 2", 99.99,"Jewelry", "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
                ProductModel("Electronic Product 3", 29.99,"Jewelry", "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
                ProductModel("Electronic Product 4", 79.99,"Jewelry", "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12))

            val categoryProductModel = CategoryProductModel(category.name, response)
            _categoryWithProductList.value.add(categoryProductModel)

            _homeApiState.value = HomeApiState.Success(_categoryWithProductList.value)
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Bloque que guarda la categoría seleccionada para ver todos los productos
    private var _currentCategory = MutableStateFlow("")

    private var _productListFromCategory = MutableStateFlow<List<ProductModel>>(emptyList())
    val productListFromCategory: StateFlow<List<ProductModel>> get() = _productListFromCategory

    fun setCurrentCategory(newCategory: String){
        _currentCategory.value = newCategory
        _productListFromCategory.value = getProductListOfCurrentCategory()
    }

    // CategoryPlp pide la lista de productos de la categoría seleccionada
    private fun getProductListOfCurrentCategory(): List<ProductModel>{
        for (categoryWithProduct in _categoryWithProductList.value){
            if (categoryWithProduct.category == _currentCategory.value){
                return categoryWithProduct.productList
            }
        }
        return emptyList()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}