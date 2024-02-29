package com.macruware.fakestore.ui.favorites.favoriteslist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.google.android.material.snackbar.Snackbar
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentFavoritesListBinding
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.FavoriteProductModel
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.ui.cart.cartdetail.CartDetailFragmentDirections
import com.macruware.fakestore.ui.favorites.favoriteslist.adapter.FavoritesAdapter
import com.macruware.fakestore.ui.main.MainViewModel
import kotlinx.coroutines.launch

class FavoritesListFragment : Fragment() {
    private var _binding: FragmentFavoritesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var favoritesAdapter: FavoritesAdapter

    private lateinit var snackBar: Snackbar

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesListBinding.inflate(layoutInflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.getFavoritesProductList()
        snackBar = Snackbar.make(binding.root, "Producto eliminado de favoritos", Snackbar.LENGTH_SHORT)
        mainViewModel.setOnBackPressedFunction { onBtnBackPressed() }
        initUI()
    }

    private fun initUI() {
        configRecycler()
        configViewModel()

        binding.fabExploreProducts.setOnClickListener {
            onBtnBackPressed()
        }
    }

    private fun configRecycler() {
        favoritesAdapter = FavoritesAdapter(
            onItemClickListener = { product : FavoriteProductModel -> goToPdp(product) },
            onFavoriteStateChange = { product : FavoriteProductModel -> onFavoriteStateChange(product) }
        )

        binding.recyclerView.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun goToPdp(favoriteProduct: FavoriteProductModel){
        snackBar.dismiss()

        findNavController().navigate(
            FavoritesListFragmentDirections
                .actionFavoritesListFragmentToHomeProductDetailFragment3(HomeFragmentProvider.FavoritesListFragment)
        )

        mainViewModel.setCurrentProduct(favoriteProduct.product)
    }

    private fun onFavoriteStateChange(favoriteProduct: FavoriteProductModel){
        mainViewModel.removeProductFromFavorites(favoriteProduct)
    }

    private fun configViewModel(){
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.favoriteProductList.collect{
                    favoritesAdapter.updateList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.isFavoriteProductDeleted.collect{
                    if (it){
                        showSnackBar()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.isEmptyFavorites.collect{
                    binding.emptyFavoriteContainer.isVisible = it
                    binding.recyclerView.isVisible = !it
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showSnackBar(){

        val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout
        val layoutParams = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        val density = resources.displayMetrics.density
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.bottomMargin = (72 * density).toInt()
        snackbarLayout.layoutParams = layoutParams

        snackBar.setAction("Deshacer") {
            mainViewModel.undoRemoveProductFromFavorites()
            snackBar.dismiss()
        }

        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                    mainViewModel.resetDeletedFavoriteProduct()
                }
            }
        })

        snackBar.show()
    }

    private fun onBtnBackPressed(){
        mainViewModel.resetDeletedFavoriteProduct()
        snackBar.dismiss()
        mainViewModel.setGoBackToHome(true)
    }
}