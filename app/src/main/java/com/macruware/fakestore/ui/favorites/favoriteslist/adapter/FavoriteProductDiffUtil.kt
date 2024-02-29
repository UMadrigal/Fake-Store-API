package com.macruware.fakestore.ui.favorites.favoriteslist.adapter

import androidx.recyclerview.widget.DiffUtil
import com.macruware.fakestore.domain.model.CartProductModel
import com.macruware.fakestore.domain.model.FavoriteProductModel

class FavoriteProductDiffUtil(
    private val oldList: List<FavoriteProductModel>,
    private val newList: List<FavoriteProductModel>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].product.name == newList[newItemPosition].product.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].isFavorite == newList[newItemPosition].isFavorite
    }

}