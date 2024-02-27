package com.macruware.fakestore.ui.cart.cartdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.CartProductModel

class CartProductAdapter(
    private var cartProductList: List<CartProductModel> = emptyList(),
    private val onItemClickListener: (CartProductModel) -> Unit,
    private val onQuantityClickListener: (CartProductModel, Boolean) -> Unit
) : RecyclerView.Adapter<CartProductViewHolder>() {

    fun updateList(list: List<CartProductModel>) {
        val cartPlDiffUtil = CartProductDiffUtil(cartProductList, list)
        val result = DiffUtil.calculateDiff(cartPlDiffUtil)

        cartProductList = list
        result.dispatchUpdatesTo(this)

//        cartProductList = list
//        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductViewHolder {
        return CartProductViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val item = cartProductList[position]
        holder.bind(item, onItemClickListener, onQuantityClickListener)
    }

    override fun getItemCount(): Int = cartProductList.size
}