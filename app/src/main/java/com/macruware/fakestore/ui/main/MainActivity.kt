package com.macruware.fakestore.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNavView : BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }

    private fun initUI() {
        configMainDrawer()
        configBottomNav()
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
    }

    private fun configBottomNav() {
        bottomNavView = binding.bottomNavView
        val navHost = supportFragmentManager.findFragmentById(R.id.mainFragmentContainerView) as NavHostFragment

        navController = navHost.navController
        bottomNavView.setupWithNavController(navController)
    }
}