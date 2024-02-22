package com.macruware.fakestore.ui.home.productdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentHomeProductDetailBinding
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.domain.model.HomeFragmentProvider.*
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.HomeViewModel
import com.macruware.fakestore.ui.home.productdetail.adapter.HomeProductDetailAdapter
import com.macruware.fakestore.ui.main.MainUiState
import com.macruware.fakestore.ui.main.MainUiState.*
import kotlinx.coroutines.launch

class HomeProductDetailFragment : Fragment() {
    private var _binding: FragmentHomeProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    private var currentProduct : ProductModel? = null
    private lateinit var homeProductDetailAdapter: HomeProductDetailAdapter

    private val previousFragment: HomeProductDetailFragmentArgs by navArgs()

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProductDetailBinding.inflate(layoutInflater, container, false)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.setMainUiState(HomeProductDetail)
        homeViewModel.setOnBackPressedFunction { onBtnBackPressed() }

        initUI()
    }

    private fun initUI() {
        configProduct()
    }

    private fun configProduct() {
        // StateFlow
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                homeViewModel.currentProduct.collect{
//                    currentProduct = it
//                }
//            }
//        }
        currentProduct = homeViewModel.currentProduct.value

        // LiveData
        homeViewModel.currentProduct.observe(requireActivity()){
            currentProduct = it
        }

        if (currentProduct != null){
            Glide.with(requireActivity())
                .load(currentProduct!!.image)
                .placeholder(R.drawable.img_place_holder)
                .into(binding.imgProduct)

            binding.tvProductName.text = currentProduct!!.name
            binding.tvProductPrice.text = currentProduct!!.price.toString()
            binding.tvProductCategory.text = currentProduct!!.category
            binding.tvProductDescription.text = currentProduct!!.description
            binding.tvReviewCounter.text = currentProduct!!.count.toString()

            configReviews()
        }
    }

    private fun configReviews() {
        homeProductDetailAdapter = HomeProductDetailAdapter()
        binding.rvReview.apply {
            adapter = homeProductDetailAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }

        homeProductDetailAdapter.setReviewList(currentProduct!!.reviewList)
    }

    private fun onBtnBackPressed(){
        // Sobreescribimos el onBackPressed y dependiendo del previousFragment, navegamos hacia el correspondiente
        when(previousFragment.fragment){
            HomeProductListFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment_to_homeProductListFragment)
            HomeCategoryPlpFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment_to_homeCategoryPlpFragment)
            HomeSearchedProductFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment_to_searchedProductFragment)
        }

        homeViewModel.setCurrentProduct(null)
    }
}