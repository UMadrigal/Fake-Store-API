package com.macruware.fakestore.ui.home.productdetail.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ItemReviewBinding
import com.macruware.fakestore.domain.model.ReviewModel

class HomeProductDetailViewHolder(view: View): ViewHolder(view){
    private val binding = ItemReviewBinding.bind(view)
    private val context = binding.root.context

    fun bind(review: ReviewModel){
        Glide.with(context)
            .load(review.profilePicture)
            .placeholder(R.drawable.img_profile_picture_place_holder)
            .into(binding.imgProfilePicture)

        binding.tvPersonName.text = review.personName
        binding.tvReviewScore.text = review.score.toString()
        binding.tvReview.text = review.reviewText

        binding.simpleRatingBar.rating = review.score.toFloat()
    }

}