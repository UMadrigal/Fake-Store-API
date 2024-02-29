package com.macruware.fakestore.ui.home.productdetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.*
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentHomeProductDetailBinding
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.FavoriteProductModel
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.domain.model.HomeFragmentProvider.*
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.productdetail.adapter.HomeProductDetailAdapter
import com.macruware.fakestore.ui.main.MainViewModel
import com.macruware.fakestore.ui.main.MainUiState.*
import kotlinx.coroutines.launch

class HomeProductDetailFragment : Fragment() {
    private var _binding: FragmentHomeProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    private var currentProduct : ProductModel? = null
    private lateinit var homeProductDetailAdapter: HomeProductDetailAdapter

    private val previousFragment: HomeProductDetailFragmentArgs by navArgs()

    private lateinit var snackBar: Snackbar
    private var snackBarMessage = ""

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
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.setMainUiState(HomeProductDetail)
        mainViewModel.setOnBackPressedFunction { onBtnBackPressed() }

        snackBar = make(binding.root, snackBarMessage, LENGTH_SHORT)

        initUI()
    }

    private fun initUI() {
        configProduct()
        configListeners()
    }

    private fun configListeners() {
        binding.btnAddToCart.setOnClickListener {
            val newCartItem = CartProductModel(1, mainViewModel.currentProduct.value!!)
            mainViewModel.addProductToCart(newCartItem)
            showSnackBar("Producto agregado al carrito")
        }

        binding.btnAddToFavorites.setOnClickListener {
            if (mainViewModel.isProductInFavorites()){
                mainViewModel.removeProductFromFavorites(FavoriteProductModel(false, mainViewModel.currentProduct.value!!))
                showSnackBar("Producto eliminado de favoritos")
            } else {
                val newFavoriteItem = FavoriteProductModel(true, mainViewModel.currentProduct.value!!)
                mainViewModel.addProductToFavorites(newFavoriteItem)
                showSnackBar("Producto agregado a favoritos")
            }
        }
    }

    private fun configProduct() {

        currentProduct = mainViewModel.currentProduct.value

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.currentProduct.collect{
                    currentProduct = it
                }
            }
        }

        if (mainViewModel.isCurrentProductInFavorites.value){
            binding.btnAddToFavorites.setImageResource(R.drawable.ic_favorite_selected)
        } else {
            binding.btnAddToFavorites.setImageResource(R.drawable.ic_favorite)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.isCurrentProductInFavorites.collect{
                    if (it){
                        binding.btnAddToFavorites.setImageResource(R.drawable.ic_favorite_selected)
                    } else {
                        binding.btnAddToFavorites.setImageResource(R.drawable.ic_favorite)
                    }
                }
            }
        }

        if (currentProduct != null){
            Glide.with(requireActivity())
                .load(currentProduct!!.image)
                .placeholder(R.drawable.img_place_holder)
                .into(binding.imgProduct)

            binding.tvProductName.text = currentProduct!!.name
            binding.tvProductPrice.append(currentProduct!!.price)
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

        var score : Float = 0.0f
        for(review in currentProduct!!.reviewList){
            score += review.score.toFloat()
        }
        score /= currentProduct!!.reviewList.size

        binding.simpleRatingBar.rating = score
    }

    @SuppressLint("RestrictedApi")
    private fun showSnackBar(message: String){
        snackBarMessage = message

        snackBar.setText(snackBarMessage)

        val snackbarLayout = snackBar.view as SnackbarLayout
        val layoutParams = snackbarLayout.layoutParams as FrameLayout.LayoutParams
        val density = resources.displayMetrics.density
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.bottomMargin = (72 * density).toInt()
        snackbarLayout.layoutParams = layoutParams

        snackBar.show()
    }

    private fun onBtnBackPressed(){
        snackBar.dismiss()
        mainViewModel.resetDeletedFavoriteProduct()
        // Sobreescribimos el onBackPressed y dependiendo del previousFragment, navegamos hacia el correspondiente
        when(previousFragment.fragment){
            HomeProductListFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment_to_homeProductListFragment)
            HomeCategoryPlpFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment_to_homeCategoryPlpFragment)
            HomeSearchedProductFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment_to_searchedProductFragment)
            HomeFragmentProvider.CartDetailFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment2_to_cartDetailFragment)
            HomeFragmentProvider.FavoritesListFragment -> findNavController().navigate(R.id.action_homeProductDetailFragment3_to_favoritesListFragment)
        }

        mainViewModel.setCurrentProduct(null)
    }
}