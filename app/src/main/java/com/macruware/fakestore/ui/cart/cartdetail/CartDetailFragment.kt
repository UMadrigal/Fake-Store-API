package com.macruware.fakestore.ui.cart.cartdetail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentCartDetailBinding
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.ProductModel
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
        initUI()
    }

    private fun initUI() {
        configRecycler()
    }

    private fun configRecycler() {
        cartProductAdapter = CartProductAdapter(
            onItemClickListener = {cartProduct : CartProductModel ->  onItemClickListener(cartProduct)},
            onQuantityClickListener = {cartProduct : CartProductModel,
                                       isIncrement : Boolean -> onQuantityClickListener(cartProduct, isIncrement)}
        )

        binding.recyclerView.apply {
            adapter = cartProductAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                homeViewModel.cartProductList.collect{
                    cartProductAdapter.updateList(it)
                }
            }
        }
    }

    private fun onItemClickListener(cartProduct: CartProductModel){
        Toast.makeText(requireActivity(), "Item seleccionado: ${cartProduct.product.name}", Toast.LENGTH_SHORT).show()
    }

    private fun onQuantityClickListener(cartProduct: CartProductModel, isIncrement: Boolean){
        if (isIncrement){
            homeViewModel.addProductToCart(cartProduct)
        } else {
            homeViewModel.removeProductFromCart(cartProduct)
        }
    }
}