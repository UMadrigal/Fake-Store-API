package com.macruware.fakestore.ui.home.plp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.macruware.fakestore.domain.model.CategoryProductModel

class CategoryProductDiffUtil(
    private val oldList: List<CategoryProductModel>,
    private val newList: List<CategoryProductModel>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].productList[0].name == newList[newItemPosition].productList[0].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}