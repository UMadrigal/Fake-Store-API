package com.macruware.fakestore.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.macruware.fakestore.domain.model.ProductModel

class ProductListDiffUtil(
    private val oldList: List<ProductModel>,
    private val newList: List<ProductModel>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].name == newList[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}