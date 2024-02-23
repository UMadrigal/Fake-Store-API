package com.macruware.fakestore.ui.home.searchedplp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.ProductModel
import com.macruware.fakestore.ui.home.ProductListDiffUtil

class SearchedProductAdapter(
    private var searchedProductList: List<ProductModel> = emptyList(),
    private var onItemClickListener: (ProductModel) -> Unit
): RecyclerView.Adapter<SearchedProductViewHolder>() {

    fun updateList(list: List<ProductModel>){
        val searchedProductDiffUtil = ProductListDiffUtil(searchedProductList, list)
        val result = DiffUtil.calculateDiff(searchedProductDiffUtil)

        searchedProductList = list
        result.dispatchUpdatesTo(this)

//        searchedProductList = list
//        notifyDataSetChanged()
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