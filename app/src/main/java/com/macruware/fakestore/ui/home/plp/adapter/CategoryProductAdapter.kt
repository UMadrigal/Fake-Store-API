package com.macruware.fakestore.ui.home.plp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel

class CategoryProductAdapter(
    private var categoryProductList: List<CategoryProductModel> = emptyList(),
    private var onBtnViewAllClickListener: (String) -> Unit,
    private var onProductClickListener: (ProductModel) -> Unit
) : RecyclerView.Adapter<CategoryProductViewHolder>() {

    fun updateList(list: List<CategoryProductModel>){
//
        val categoryProductDiffUtil = CategoryProductDiffUtil(categoryProductList, list)
        val result = DiffUtil.calculateDiff(categoryProductDiffUtil)

        categoryProductList = list
        result.dispatchUpdatesTo(this)

//        categoryProductList = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        return CategoryProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category_and_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        val item = categoryProductList[position]
        holder.bind(item, onBtnViewAllClickListener, onProductClickListener)
    }

    override fun getItemCount(): Int = categoryProductList.size
}