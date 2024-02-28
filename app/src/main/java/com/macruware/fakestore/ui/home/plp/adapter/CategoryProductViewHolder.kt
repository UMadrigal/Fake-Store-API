package com.macruware.fakestore.ui.home.plp.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.macruware.fakestore.databinding.ItemCategoryAndProductBinding
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.categoryplp.adapter.CategoryPlpAdapter

class CategoryProductViewHolder(view:View): ViewHolder(view) {
    private val binding = ItemCategoryAndProductBinding.bind(view)

    private lateinit var categoryPlpAdapter: CategoryPlpAdapter

    private val context = binding.root.context

    fun bind(
        item: CategoryProductModel,
        onBtnViewAllClickListener: (String) -> Unit,
        onProductClickListener: (ProductModel) -> Unit)
    {
        if (item.productList.size >= 2){

            binding.tvCategory.text = item.category
            binding.btnViewAll.setOnClickListener {
                onBtnViewAllClickListener(item.category)
            }

            val productList = listOf(
                item.productList[0],
                item.productList[1]
            )
            configRecycler(onProductClickListener)
            categoryPlpAdapter.updateList(productList)
        }
    }

    private fun configRecycler(onProductClickListener: (ProductModel) -> Unit) {
        categoryPlpAdapter = CategoryPlpAdapter(onItemClickListener = {product: ProductModel -> onProductClickListener(product) })

        binding.rvCategoryProduct.apply {
            adapter = categoryPlpAdapter
            // requireActivity()
            layoutManager = GridLayoutManager(context,2)
        }
    }
}