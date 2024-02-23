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
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.HomeViewModel
import com.macruware.fakestore.ui.home.searchedplp.adapter.SearchedProductAdapter
import com.macruware.fakestore.ui.main.MainUiState.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeSearchedProductFragment : Fragment() {
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
        homeViewModel.setMainUiState(HomeSearchedProduct)
        homeViewModel.setOnBackPressedFunction { onBtnBackPressed() }

        initUI()
    }

    private fun initUI() {
        configRecycler()
    }

    private fun configRecycler() {
        searchedProductAdapter = SearchedProductAdapter(
            onItemClickListener = {product: ProductModel -> goToPdp(product)})

        binding.recyclerView.apply {
            adapter = searchedProductAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        searchedProductAdapter.updateList(homeViewModel.getAllProducts())
    }

    private fun goToPdp(product: ProductModel){
        findNavController().navigate(
            HomeSearchedProductFragmentDirections
                .actionSearchedProductFragmentToHomeProductDetailFragment(
                    HomeFragmentProvider.HomeSearchedProductFragment
                )
        )

        homeViewModel.setCurrentProduct(product)

    }

    private fun reSearchQuery(){
//        Toast.makeText(requireActivity(), "reSearchQuery", Toast.LENGTH_SHORT).show()
        searchedProductAdapter.updateList(homeViewModel.reSearchQuery())
    }

    private fun onBtnBackPressed(){
        findNavController().navigate(R.id.action_searchedProductFragment_to_homeProductListFragment)
    }
}