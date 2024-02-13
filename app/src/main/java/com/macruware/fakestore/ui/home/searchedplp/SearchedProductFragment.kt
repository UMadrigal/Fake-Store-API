package com.macruware.fakestore.ui.home.searchedplp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentSearchedProductBinding
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.HomeViewModel
import com.macruware.fakestore.ui.home.categoryplp.adapter.CategoryPlpAdapter
import com.macruware.fakestore.ui.home.searchedplp.adapter.SearchedProductAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchedProductFragment : Fragment() {
    private var _binding: FragmentSearchedProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var searchedProductAdapter: SearchedProductAdapter

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchedProductBinding.inflate(layoutInflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.setLambdaFunction { reSearchQuery() }

        initUI()
    }

    private fun initUI() {
        configRecycler()
        apiStateSuccess()
    }

    private fun apiStateSuccess() {
        val electronicsProducts = listOf(
            ProductModel("Electronic Product 1", 49.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.5, 10),
            ProductModel("Electronic Product 2", 99.99, "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 8),
            ProductModel("Electronic Product 3", 29.99, "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 15),
            ProductModel("Electronic Product 4", 79.99, "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 12),
            ProductModel("Electronic Product 5", 59.99, "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 20))

        searchedProductAdapter.updateList(electronicsProducts)
    }

    private fun configRecycler() {
        searchedProductAdapter = SearchedProductAdapter(
            onItemClickListener = {product: ProductModel -> goToPdp(product)})

        binding.recyclerView.apply {
            adapter = searchedProductAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
    }

    private fun goToPdp(product: ProductModel){
        Toast.makeText(requireActivity(),
            "Ver producto ${product.name}, precio: ${product.price}",
            Toast.LENGTH_SHORT).show()
    }

    private fun reSearchQuery(){
        Toast.makeText(requireActivity(), "reSearchQuery", Toast.LENGTH_SHORT).show()
    }

    private fun onBtnBackPressed(){
        findNavController().navigate(R.id.action_searchedProductFragment_to_homeProductListFragment)
    }
}