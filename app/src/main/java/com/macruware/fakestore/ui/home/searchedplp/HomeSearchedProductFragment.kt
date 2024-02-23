package com.macruware.fakestore.ui.home.searchedplp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentSearchedProductBinding
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.HomeViewModel
import com.macruware.fakestore.ui.home.searchedplp.SearchedApiState
import com.macruware.fakestore.ui.home.searchedplp.adapter.SearchedProductAdapter
import com.macruware.fakestore.ui.main.MainUiState.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        homeViewModel.getAllProducts()

        initUI()
    }

    private fun initUI() {
        configRecycler()
        configSearchedApiState()
    }

    private fun configSearchedApiState(){
        lifecycleScope.launch {
            repeatOnLifecycle(State.STARTED){
                homeViewModel.searchedApiState.collect{
                    when(it){
                        is SearchedApiState.Loading -> searchedApiStateLoading()
                        is SearchedApiState.Success -> searchedApiStateSuccess(it)
                        is SearchedApiState.Error -> searchedApiStateError(it)
                    }
                }
            }
        }
    }

    private fun searchedApiStateLoading() {
        binding.progressBar.isVisible = true
    }

    private fun searchedApiStateSuccess(state: SearchedApiState.Success) {
        binding.progressBar.isVisible = false
        searchedProductAdapter.updateList(state.productList)
    }

    private fun searchedApiStateError(state: SearchedApiState.Error) {
        binding.progressBar.isVisible = false
        Toast.makeText(requireActivity(), state.error, Toast.LENGTH_LONG).show()
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
        findNavController().navigate(
            HomeSearchedProductFragmentDirections
                .actionSearchedProductFragmentToHomeProductDetailFragment(
                    HomeFragmentProvider.HomeSearchedProductFragment
                )
        )

        homeViewModel.setCurrentProduct(product)

    }

    private fun reSearchQuery(){
        searchedProductAdapter.updateList(homeViewModel.reSearchQuery())
    }

    private fun onBtnBackPressed(){
        findNavController().navigate(R.id.action_searchedProductFragment_to_homeProductListFragment)
    }
}