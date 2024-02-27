package com.macruware.fakestore.ui.cart.cartdetail.adapter

import androidx.recyclerview.widget.DiffUtil
import com.macruware.fakestore.domain.model.CartProductModel

class CartProductDiffUtil(
    private val oldList: List<CartProductModel>,
    private val newList: List<CartProductModel>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].product.name == newList[newItemPosition].product.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].quantity == newList[newItemPosition].quantity
    }

}