package com.macruware.fakestore.ui.home.categoryplp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentHomeCategoryPlpBinding
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.categoryplp.adapter.CategoryPlpAdapter
import com.macruware.fakestore.ui.main.MainViewModel
import com.macruware.fakestore.ui.main.MainUiState.HomeCategoryPlp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeCategoryPlpFragment : Fragment() {
    private var _binding: FragmentHomeCategoryPlpBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }

    private lateinit var categoryPlpAdapter: CategoryPlpAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCategoryPlpBinding.inflate(layoutInflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.setLambdaFunction{ searchIntoCategory() }
        mainViewModel.setOnBackPressedFunction { onBtnBackPressed() }
        mainViewModel.setMainUiState(HomeCategoryPlp)

        initUI()
    }

    private fun searchIntoCategory() {
        mainViewModel.searchIntoCategory()
    }

    private fun initUI() {
        configRecycler()
        fetchData()
    }

    private fun fetchData(){
        categoryPlpAdapter.updateList(mainViewModel.productListFromCategory.value)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.productListFromCategory.collect{
                    categoryPlpAdapter.updateList(it)
                }
            }
        }
    }

    private fun configRecycler() {
        categoryPlpAdapter = CategoryPlpAdapter(
            onItemClickListener = {product: ProductModel -> goToPdp(product)})

        binding.recyclerView.apply {
            adapter = categoryPlpAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
        }
    }

    private fun goToPdp(product: ProductModel){
        findNavController().navigate(
            HomeCategoryPlpFragmentDirections
                .actionHomeCategoryPlpFragmentToHomeProductDetailFragment(
                    HomeFragmentProvider.HomeCategoryPlpFragment
                )
        )

        mainViewModel.setCurrentProduct(product)

    }
    
    private fun onBtnBackPressed(){
        findNavController().navigate(R.id.action_homeCategoryPlpFragment_to_homeProductListFragment)
    }

}