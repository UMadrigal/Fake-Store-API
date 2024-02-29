package com.macruware.fakestore.ui.favorites.favoriteslist.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ItemFavoriteProductBinding
import com.macruware.fakestore.domain.model.FavoriteProductModel

class FavoritesViewHolder(view:View): ViewHolder(view) {
    private val binding = ItemFavoriteProductBinding.bind(view)
    private var context = binding.root.context

    fun bind(item: FavoriteProductModel,
             onItemClickListener: (FavoriteProductModel) -> Unit,
             onFavoriteStateChange: (FavoriteProductModel) -> Unit) {

        Glide.with(context)
            .load(item.product.image)
            .placeholder(R.drawable.img_place_holder)
            .into(binding.imgProduct)

        binding.tvProductName.text = item.product.name

        binding.tvProductPrice.text = "$" + item.product.price

        binding.btnFavorite.setOnClickListener {
            onFavoriteStateChange(item)
        }

        binding.containerCard.setOnClickListener {
            onItemClickListener(item)
        }
    }
}