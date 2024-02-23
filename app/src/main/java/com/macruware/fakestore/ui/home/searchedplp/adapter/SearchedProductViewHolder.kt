package com.macruware.fakestore.ui.home.searchedplp.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ItemSearchedProductBinding
import com.macruware.fakestore.domain.model.ProductModel

class SearchedProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var binding = ItemSearchedProductBinding.bind(view)
    private val context = binding.root.context

    fun bind(item: ProductModel, onItemClickListener: (ProductModel) -> Unit) {

        setProduct(item, binding.imgProduct)

        binding.tvProductName.text = item.name

        binding.tvProductDescription.text = item.description

        binding.tvProductPrice.text = "$" + item.price

        binding.containerCard.setOnClickListener {
            onItemClickListener(item)
        }

    }

    private fun setProduct(product: ProductModel, view: ImageView) {
        Glide.with(context)
            .load(product.image)
            .placeholder(R.drawable.img_place_holder)
            .into(view)
    }
}