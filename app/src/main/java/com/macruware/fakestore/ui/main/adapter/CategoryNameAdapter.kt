package com.macruware.fakestore.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.CategoryNameModel

class CategoryNameAdapter(private var categoryNameList: List<CategoryNameModel> = emptyList()) : RecyclerView.Adapter<CategoryNameViewHolder>() {

    fun updateList(list: List<CategoryNameModel>){
        categoryNameList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryNameViewHolder {
        return CategoryNameViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_category_name, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryNameViewHolder, position: Int) {
        val item = categoryNameList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = categoryNameList.size
}