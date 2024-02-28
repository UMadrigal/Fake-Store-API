package com.macruware.fakestore.ui.home.plp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentHomeProductListBinding
import com.macruware.fakestore.domain.model.HomeFragmentProvider
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.HomeApiState
import com.macruware.fakestore.ui.home.plp.adapter.CategoryProductAdapter
import com.macruware.fakestore.ui.main.MainViewModel
import com.macruware.fakestore.ui.main.MainUiState.HomeProductList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeProductListFragment : Fragment() {
    private var _binding: FragmentHomeProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }

    private lateinit var categoryProductAdapter: CategoryProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProductListBinding.inflate(layoutInflater,container,false)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.setLambdaFunction { goToSearched() }
        mainViewModel.setLambdaFunctionForCategory { category: String -> goToCategoryPlp(category) }
        mainViewModel.setMainUiState(HomeProductList)

        initUI()
    }

    private fun goToSearched(){
        findNavController().navigate(R.id.action_homeProductListFragment_to_searchedProductFragment)
    }

    private fun initUI() {
        configRecycler()
        configHomeApiState()
    }

    private fun configHomeApiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                mainViewModel.homeApiState.collect{
                    when(it){
                        is HomeApiState.Loading -> homeApiStateLoading()
                        is HomeApiState.Success -> homeApiStateSuccess(it)
                        is HomeApiState.Error -> homeApiStateError(it)
                    }
                }
            }
        }
    }

    private fun homeApiStateLoading() {
        binding.progressBar.isVisible = true
    }

    private fun homeApiStateSuccess(state: HomeApiState.Success){
        binding.progressBar.isVisible = false
        categoryProductAdapter.updateList(state.categoryWithProductList)
    }

    private fun homeApiStateError(state: HomeApiState.Error){
        binding.progressBar.isVisible = false
        Toast.makeText(requireActivity(), state.error, Toast.LENGTH_LONG).show()
    }

    private fun configRecycler() {
        categoryProductAdapter = CategoryProductAdapter(
            onBtnViewAllClickListener =  { category: String -> goToCategoryPlp(category)},
            onProductClickListener = {product: ProductModel -> goToPdp(product)}
        )
        binding.recyclerView.apply {
            adapter = categoryProductAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun goToCategoryPlp(category: String){
        mainViewModel.setCurrentCategory(category)
        findNavController().navigate(R.id.action_homeProductListFragment_to_homeCategoryPlpFragment)
    }

    private fun goToPdp(product: ProductModel){
        findNavController().navigate(
            HomeProductListFragmentDirections
                .actionHomeProductListFragmentToHomeProductDetailFragment(
                    HomeFragmentProvider.HomeProductListFragment)
        )

        mainViewModel.setCurrentProduct(product)

    }

    private fun onBtnBackPressed(){
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog
            .setTitle(getString(R.string.app_name))
            .setMessage(getString(R.string.text_alert_on_exit_app))

        alertDialog.setPositiveButton(getString(R.string.text_exit)) { _, _ ->
            requireActivity().finish()
        }
        alertDialog.setNegativeButton(getString(R.string.text_cancel)) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}