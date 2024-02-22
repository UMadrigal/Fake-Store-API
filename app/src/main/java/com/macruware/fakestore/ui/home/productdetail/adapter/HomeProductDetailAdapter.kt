package com.macruware.fakestore.ui.home.productdetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macruware.fakestore.R
import com.macruware.fakestore.domain.model.ReviewModel

class HomeProductDetailAdapter(private var reviewList: List<ReviewModel> = emptyList()): RecyclerView.Adapter<HomeProductDetailViewHolder>() {

    fun setReviewList(list: List<ReviewModel>) {
        reviewList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductDetailViewHolder {
        return HomeProductDetailViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeProductDetailViewHolder, position: Int) {
        val item = reviewList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = reviewList.size
}