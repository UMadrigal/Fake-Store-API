package com.macruware.fakestore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.macruware.fakestore.databinding.FragmentHomeContainerBinding

class HomeContainerFragment : Fragment() {
    private var _binding: FragmentHomeContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}