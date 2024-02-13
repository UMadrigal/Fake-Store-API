package com.macruware.fakestore.ui.home.plp.adapter

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ItemCategoryAndProductBinding
import com.macruware.fakestore.domain.model.CategoryProductModel
import com.macruware.fakestore.domain.model.ProductModel

class CategoryProductViewHolder(view:View): ViewHolder(view) {
    private val binding = ItemCategoryAndProductBinding.bind(view)
    private val context = binding.root.context

    fun bind(item: CategoryProductModel, onItemClickListener: (CategoryProductModel) -> Unit){
        binding.tvCategory.text = item.category
        val product = item.productList[0]
        val product2 = item.productList[1]

        setProduct(product, binding.imgProduct)
        setProduct(product2, binding.imgProduct2)

        binding.tvProductName.text = product.name
        binding.tvProductName2.text = product2.name

        binding.tvProductPrice.text = product.price.toString()
        binding.tvProductPrice2.text = product2.price.toString()

        binding.btnViewAll.setOnClickListener {
            onItemClickListener(item)
        }

    }

    private fun setProduct(product: ProductModel, view: ImageView){
        Glide.with(context)
            .load(product.image)
            .placeholder(R.drawable.img_place_holder)
            .into(view)
    }
}