package com.macruware.fakestore.ui.main

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ActivityMainBinding
import com.macruware.fakestore.ui.main.MainApiState.Error
import com.macruware.fakestore.ui.main.MainApiState.Loading
import com.macruware.fakestore.ui.main.MainApiState.Success
import com.macruware.fakestore.ui.main.MainUiState.*
import com.macruware.fakestore.ui.main.adapter.CategoryNameAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private lateinit var bottomNavView : BottomNavigationView
    private lateinit var navController: NavController

    private lateinit var categoryNameAdapter: CategoryNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initUI()
    }

    private fun initUI() {
        mainViewModel.getCategories()

        configMainDrawer()
        configBottomNav()
        configSearchBar()
        configUiState()
        configListeners()

        configApiState()
    }

    private fun configApiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                mainViewModel.mainApiState.collect{
                    when(it){
                        is Loading -> apiStateLoading()
                        is Success -> apiStateSuccess(it)
                        is Error -> apiStateError(it)
                    }
                }
            }
        }
    }

    private fun apiStateLoading() {

    }

    private fun apiStateSuccess(state: Success){

        categoryNameAdapter.updateList(state.categoryNameList)
    }

    private fun apiStateError(state: Error){
        Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun configUiState() {
        // get state from viewModel
        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                mainViewModel.mainUiState.collect{
                    when(it){
                        HomeProductList -> uiStateHomeProductList()
                        HomeCategoryPlp -> uiStateHomeCategoryPlp()
                        HomeSearchedProduct -> uiStateHomeSearchedProduct()
                        HomeProductDetail -> uiStateHomeProductDetail()
                        CartDetailFragment -> uiStateCartDetailFragment()
                        FavoritesListFragment -> uiStateFavoritesListFragment()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                mainViewModel.goBackToHome.collect{
                    if (it){
                        goBackToHome()
                        mainViewModel.setGoBackToHome(false)
                    }
                }
            }
        }
    }

    private fun uiStateHomeProductList() {
        binding.btnMenu.isVisible = true
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        binding.btnBack.isVisible = false
        binding.searchContainer.isVisible = true
        binding.etSearch.text.clear()
        binding.tvTitle.isVisible = false
        binding.btnNotifications.isVisible = true
        binding.bottomNavView.isVisible = true
    }

    private fun uiStateHomeCategoryPlp() {
        binding.btnMenu.isVisible = false
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.btnBack.isVisible = true
        binding.searchContainer.isVisible = true
        binding.tvTitle.isVisible = false
        binding.btnNotifications.isVisible = false
        binding.bottomNavView.isVisible = true
    }

    private fun uiStateHomeSearchedProduct() {
        binding.btnMenu.isVisible = false
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.btnBack.isVisible = true
        binding.searchContainer.isVisible = true
        binding.tvTitle.isVisible = false
        binding.btnNotifications.isVisible = false
        binding.bottomNavView.isVisible = true
    }

    private fun uiStateHomeProductDetail() {
        binding.btnMenu.isVisible = false
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.btnBack.isVisible = true
        binding.searchContainer.isVisible = false
        binding.tvTitle.isVisible = false
        binding.btnNotifications.isVisible = false
        binding.bottomNavView.isVisible = false
    }

    private fun uiStateCartDetailFragment() {
        binding.btnMenu.isVisible = false
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.btnBack.isVisible = true
        binding.searchContainer.isVisible = false
        binding.tvTitle.isVisible = true
        binding.tvTitle.text = getString(R.string.text_title_cart_fragment)
        binding.btnNotifications.isVisible = false
        binding.bottomNavView.isVisible = false
    }

    private fun uiStateFavoritesListFragment() {
        binding.btnMenu.isVisible = false
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        binding.btnBack.isVisible = true
        binding.searchContainer.isVisible = false
        binding.tvTitle.isVisible = true
        binding.tvTitle.text = getString(R.string.text_title_favorites_fragment)
        binding.btnNotifications.isVisible = false
        binding.bottomNavView.isVisible = false
    }

    private fun configCategoriesRecycler() {
        categoryNameAdapter = CategoryNameAdapter(
            onCategorySelected = { category: String -> onCategorySelected(category) }
        )

        binding.rvCategories.apply {
            adapter = categoryNameAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun onCategorySelected(category: String){
        binding.drawerLayout.close()
        mainViewModel.goToCategory(category)
    }

    private fun configListeners() {
        binding.btnBack.setOnClickListener {
            mainViewModel.onBackPressed.value?.let { it() }
        }
    }

    private fun configBottomNav() {
        bottomNavView = binding.bottomNavView
        val navHost = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment
        navController = navHost.navController
        bottomNavView.setupWithNavController(navController)
    }

    private fun goBackToHome(){
        binding.bottomNavView.selectedItemId = R.id.homeContainerFragment
    }

    private fun configSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                // Ocultar el teclado
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)

                // Quitar el foco del EditText
                binding.etSearch.clearFocus()

                val query = binding.etSearch.text.toString().trim()
                if (query.isNotEmpty()){
                    mainViewModel.setQuery(query)
                } else {
                    mainViewModel.onBackPressed.value?.let { it() }
                }

                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun configMainDrawer() {
        val navigationView = binding.navigationView
        val mainDrawer = binding.drawerLayout

        // Configuración del ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(this, mainDrawer, binding.header,
            R.string.nav_open,
            R.string.nav_close
        )
        mainDrawer.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false

        // Configuración drawer para abrir y cerrar el cajón de navegación
        val burgerIcon = binding.btnMenu
        burgerIcon.setOnClickListener {
            if (mainDrawer.isDrawerOpen(GravityCompat.START)) {
                mainDrawer.closeDrawer(GravityCompat.START)
            } else {
                mainDrawer.openDrawer(GravityCompat.START)
            }
        }

        // Calcula el ancho de la pantalla
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels

        // Calcula el ancho deseado para el NavigationView (75% del ancho de la pantalla)
        val desiredWidth = (0.75 * screenWidth).toInt()

        // Establece el ancho del NavigationView
        val layoutParams = navigationView.layoutParams
        layoutParams.width = desiredWidth
        navigationView.layoutParams = layoutParams

        configCategoriesRecycler()
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
}