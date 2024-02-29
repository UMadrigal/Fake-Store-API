package com.macruware.fakestore.ui.cart.cartdetail.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ItemCartProductBinding
import com.macruware.fakestore.domain.model.CartProductModel

class CartProductViewHolder(view: View): ViewHolder(view){
    private var binding = ItemCartProductBinding.bind(view)
    private var context = binding.root.context

    fun bind(item: CartProductModel,
             onItemClickListener: (CartProductModel) -> Unit,
             onQuantityClickListener: (CartProductModel, Boolean) -> Unit) {

        Glide.with(context)
            .load(item.product.image)
            .placeholder(R.drawable.img_place_holder)
            .into(binding.imgProduct)

        binding.tvProductName.text = item.product.name

        binding.tvProductPrice.text = "$" + item.product.price

        binding.tvCounter.text = item.quantity.toString()

        binding.btnDecrement.setOnClickListener {
            onQuantityClickListener(item, false)
        }

        binding.btnIncrement.setOnClickListener {
            onQuantityClickListener(item, true)
        }

        binding.containerCard.setOnClickListener {
            onItemClickListener(item)
        }
    }
}