package com.macruware.fakestore.ui.home.plp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentHomeProductListBinding
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.HomeViewModel
import com.macruware.fakestore.ui.home.plp.adapter.CategoryProductAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeProductListFragment : Fragment() {
    private var _binding: FragmentHomeProductListBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

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
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.setLambdaFunction { goToSearched() }

        initUI()
    }

    private fun goToSearched(){
        findNavController().navigate(R.id.action_homeProductListFragment_to_searchedProductFragment)
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

        val jewelryProducts = listOf(
            ProductModel("Jewelry Product 1", 149.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.7, 5),
            ProductModel("Jewelry Product 2", 199.99, "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.1, 7),
            ProductModel("Jewelry Product 3", 99.99, "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.9, 8),
            ProductModel("Jewelry Product 4", 179.99, "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.4, 6),
            ProductModel("Jewelry Product 5", 129.99, "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.8, 10))

        val mensClothingProducts = listOf(
            ProductModel("Men's Clothing Product 1", 34.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.6, 15),
            ProductModel("Men's Clothing Product 2", 44.99, "Description 2", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.7, 20),
            ProductModel("Men's Clothing Product 3", 24.99, "Description 3", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.2, 18),
            ProductModel("Men's Clothing Product 4", 54.99, "Description 4", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.0, 12),
            ProductModel("Men's Clothing Product 5", 39.99, "Description 5", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 3.5, 22))

        val womensClothingProducts = listOf(
            ProductModel("Women's Clothing Product 1", 29.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 8),
            ProductModel("Women's Clothing Product 1", 29.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 8),
            ProductModel("Women's Clothing Product 1", 29.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 8),
            ProductModel("Women's Clothing Product 1", 29.99, "Description 1", "https://fakestoreapi.com/img/71-3HjGNDUL._AC_SY879._SX._UX._SY._UY_.jpg", 4.8, 8))

        val categoryProductList = listOf(
            CategoryProductModel("electronics", electronicsProducts),
            CategoryProductModel("jewelery", jewelryProducts),
            CategoryProductModel("men's clothing", mensClothingProducts),
            CategoryProductModel("women's clothing", womensClothingProducts))

        categoryProductAdapter.updateList(categoryProductList)

    }

    private fun configRecycler() {
        categoryProductAdapter = CategoryProductAdapter(
            onBtnViewAllClickListener =  { category: CategoryProductModel -> goToCategoryPlp(category)},
            onProductClickListener = {product: ProductModel -> goToPdp(product)}
        )
        binding.recyclerView.apply {
            adapter = categoryProductAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL,false)
        }
    }

    private fun goToCategoryPlp(category: CategoryProductModel){
        Toast.makeText(requireActivity(),
            "Ver todo de categoría: ${category.category}, ${category.productList.size} productos.",
            Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_homeProductListFragment_to_homeCategoryPlpFragment)
    }

    private fun goToPdp(product: ProductModel){
        Toast.makeText(requireActivity(),
            "Ver producto ${product.name}, precio: ${product.price}",
            Toast.LENGTH_SHORT).show()
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