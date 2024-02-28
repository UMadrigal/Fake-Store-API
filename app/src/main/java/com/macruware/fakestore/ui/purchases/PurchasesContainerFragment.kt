package com.macruware.fakestore.ui.purchases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.macruware.fakestore.databinding.FragmentPurchasesContainerBinding

class PurchasesContainerFragment : Fragment() {
    private var _binding: FragmentPurchasesContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasesContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}