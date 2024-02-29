package com.macruware.fakestore.ui.favorites.favoriteslist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.FavoriteProductModel
import com.macruware.fakestore.ui.cart.cartdetail.adapter.CartProductDiffUtil

class FavoritesAdapter(private var favoritesList: List<FavoriteProductModel> = emptyList(),
                       private val onItemClickListener: (FavoriteProductModel) -> Unit,
                       private val onFavoriteStateChange: (FavoriteProductModel) -> Unit
) : RecyclerView.Adapter<FavoritesViewHolder>() {

    fun updateList(list: List<FavoriteProductModel>){
        val favoritePlDiffUtil = FavoriteProductDiffUtil(favoritesList, list)
        val result = DiffUtil.calculateDiff(favoritePlDiffUtil)

        favoritesList = list
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        return FavoritesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = favoritesList[position]
        holder.bind(item, onItemClickListener, onFavoriteStateChange)
    }

    override fun getItemCount(): Int = favoritesList.size
}
