package com.macruware.fakestore.ui.cart.cartdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.macruware.fakestore.databinding.FragmentCartDetailBinding
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.ui.cart.cartdetail.adapter.CartProductAdapter
import com.macruware.fakestore.ui.main.MainViewModel
import kotlinx.coroutines.launch

class CartDetailFragment : Fragment() {
    private var _binding: FragmentCartDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel

    private lateinit var cartProductAdapter: CartProductAdapter

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
        _binding = FragmentCartDetailBinding.inflate(layoutInflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.getCartProductList()
        snackBar = Snackbar.make(binding.root, "Producto eliminado", Snackbar.LENGTH_SHORT)
        mainViewModel.setOnBackPressedFunction { onBtnBackPressed() }

        initUI()
    }

    private fun initUI() {
        configRecycler()
        configViewModel()
        configListeners()
    }

    private fun configListeners() {
        binding.btnCheckout.setOnClickListener {
            // start activity checkout
        }

        binding.fabExploreProducts.setOnClickListener {
            onBtnBackPressed()
        }
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
                mainViewModel.cartProductList.collect{
                    cartProductAdapter.updateList(it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                mainViewModel.isCartProductDeleted.collect{
                    if (it){
                        showSnackBar()
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                mainViewModel.isEmptyCart.collect{
                    binding.emptyCartContainer.isVisible = it
                    binding.cartContainer.isVisible = !it
                }
            }
        }
    }


    @SuppressLint("RestrictedApi")
    private fun showSnackBar(){

        val snackbarLayout = snackBar.view as SnackbarLayout
        val layoutParams = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        val density = resources.displayMetrics.density
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.bottomMargin = (72 * density).toInt()
        snackbarLayout.layoutParams = layoutParams

        snackBar.setAction("Deshacer") {
            mainViewModel.undoRemoveProductFromCart()
            snackBar.dismiss()
        }

        snackBar.addCallback(object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                if (event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT) {
                    mainViewModel.resetDeletedCartProduct()
                }
            }
        })

        snackBar.show()
    }

    private fun goToPdp(cartProduct: CartProductModel){
        snackBar.dismiss()

        findNavController().navigate(
            CartDetailFragmentDirections
                .actionCartDetailFragmentToHomeProductDetailFragment2(HomeFragmentProvider.CartDetailFragment)
        )

        mainViewModel.setCurrentProduct(cartProduct.product)
    }

    private fun onQuantityClickListener(cartProduct: CartProductModel, isIncrement: Boolean){
        if (isIncrement){
            mainViewModel.addProductToCart(cartProduct)
        } else {
            mainViewModel.removeProductFromCart(cartProduct)
        }
    }

    private fun onBtnBackPressed(){
        mainViewModel.resetDeletedCartProduct()
        snackBar.dismiss()
        mainViewModel.setGoBackToHome(true)
    }
}