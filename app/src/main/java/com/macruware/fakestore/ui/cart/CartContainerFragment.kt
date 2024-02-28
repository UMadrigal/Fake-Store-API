package com.macruware.fakestore.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.macruware.fakestore.databinding.FragmentCartContainerBinding

class CartContainerFragment : Fragment() {
    private var _binding: FragmentCartContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}