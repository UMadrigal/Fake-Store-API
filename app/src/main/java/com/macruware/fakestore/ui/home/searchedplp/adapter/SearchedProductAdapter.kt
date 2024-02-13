package com.macruware.fakestore.ui.home.searchedplp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.ProductModel

class SearchedProductAdapter(
    private var searchedProductList: List<ProductModel> = emptyList(),
    private var onItemClickListener: (ProductModel) -> Unit
): RecyclerView.Adapter<SearchedProductViewHolder>() {

    fun updateList(list: List<ProductModel>){
        searchedProductList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedProductViewHolder {
        return SearchedProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_searched_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchedProductViewHolder, position: Int) {
        val item = searchedProductList[position]
        holder.bind(item, onItemClickListener)
    }

    override fun getItemCount(): Int = searchedProductList.size
}