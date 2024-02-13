package com.macruware.fakestore.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.CategoryProductModel

class CategoryProductAdapter(
    private var categoryProductList: List<CategoryProductModel> = emptyList(),
    private var onItemClickListener: (CategoryProductModel) -> Unit
) : RecyclerView.Adapter<CategoryProductViewHolder>() {
    fun updateList(list: List<CategoryProductModel>){
        categoryProductList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        return CategoryProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category_and_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        val item = categoryProductList[position]
        holder.bind(item, onItemClickListener)
    }

    override fun getItemCount(): Int = categoryProductList.size
}