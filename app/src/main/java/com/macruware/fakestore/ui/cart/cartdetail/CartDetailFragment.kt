package com.macruware.fakestore.ui.cart.cartdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.util.TypedValueCompat.dpToPx
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.macruware.fakestore.databinding.FragmentCartDetailBinding
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.ui.cart.cartdetail.adapter.CartProductAdapter
import com.macruware.fakestore.ui.home.HomeViewModel
import kotlinx.coroutines.launch

class CartDetailFragment : Fragment() {
    private var _binding: FragmentCartDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var cartProductAdapter: CartProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartDetailBinding.inflate(layoutInflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.getCartProductList()
        homeViewModel.setOnBackPressedFunction { requireActivity().onBackPressed() }

        initUI()
    }

    private fun initUI() {
        configRecycler()
        configViewModel()
    }

    private fun configRecycler() {
        cartProductAdapter = CartProductAdapter(
            onItemClickListener = {cartProduct : CartProductModel ->  goToPdp(cartProduct)},
            onQuantityClickListener = {cartProduct : CartProductModel,
                                       isIncrement : Boolean -> onQuantityClickListener(cartProduct, isIncrement)}
        )

        binding.recyclerView.apply {
            adapter = cartProductAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
        }


    }

    private fun configViewModel(){
        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                homeViewModel.cartProductList.collect{
                    cartProductAdapter.updateList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                homeViewModel.isCartProductDeleted.collect{
                    if (it){
                        showSnackBar()
                    }
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun showSnackBar(){
        val rootView = binding.root

        val snackbar = Snackbar.make(
            rootView,
            "Producto eliminado",
            Snackbar.LENGTH_SHORT
        )

        val snackbarLayout = snackbar.view as SnackbarLayout
        val layoutParams = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        val density = resources.displayMetrics.density
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.bottomMargin = (72 * density).toInt()
        snackbarLayout.layoutParams = layoutParams

        snackbar.setAction("Deshacer") {
            homeViewModel.undoRemoveProductFromCart()
            snackbar.dismiss()
        }

        snackbar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                    homeViewModel.resetDeletedCartProduct()
                }
            }
        })

        snackbar.show()
    }

    private fun goToPdp(cartProduct: CartProductModel){
        findNavController().navigate(
            CartDetailFragmentDirections
                .actionCartDetailFragmentToHomeProductDetailFragment2(HomeFragmentProvider.CartDetailFragment)
        )

        homeViewModel.setCurrentProduct(cartProduct.product)
    }

    private fun onQuantityClickListener(cartProduct: CartProductModel, isIncrement: Boolean){
        if (isIncrement){
            homeViewModel.addProductToCart(cartProduct)
        } else {
            homeViewModel.removeProductFromCart(cartProduct)
        }
    }
}