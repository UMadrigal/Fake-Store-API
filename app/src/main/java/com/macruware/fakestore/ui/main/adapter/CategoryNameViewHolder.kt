package com.macruware.fakestore.ui.main.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.macruware.fakestore.databinding.ItemCategoryNameBinding
import com.macruware.fakestore.domain.model.CategoryNameModel

class CategoryNameViewHolder(view: View) : ViewHolder(view) {

    private val binding = ItemCategoryNameBinding.bind(view)

    fun bind(item: CategoryNameModel, onCategorySelected: (String) -> Unit) {
        binding.tvDrawerMenuCategoryName.text = item.name
        binding.tvDrawerMenuCategoryName.setOnClickListener {
            onCategorySelected(item.name)
        }
    }

}