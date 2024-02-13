package com.macruware.fakestore.ui.home.categoryplp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.ProductModel

class CategoryPlpAdapter(
    private var categoryPlpList: List<ProductModel> = emptyList(),
    private var onItemClickListener: (ProductModel) -> Unit
): RecyclerView.Adapter<CategoryPlpViewHolder>() {

    fun updateList(list: List<ProductModel>){
        categoryPlpList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryPlpViewHolder {
        return CategoryPlpViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryPlpViewHolder, position: Int) {
        val item = categoryPlpList[position]
        holder.bind(item, onItemClickListener)
    }

    override fun getItemCount(): Int = categoryPlpList.size
}