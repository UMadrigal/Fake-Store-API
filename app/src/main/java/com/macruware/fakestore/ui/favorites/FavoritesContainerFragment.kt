package com.macruware.fakestore.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.macruware.fakestore.databinding.FragmentFavoritesContainerBinding

class FavoritesContainerFragment : Fragment() {
    private var _binding: FragmentFavoritesContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}