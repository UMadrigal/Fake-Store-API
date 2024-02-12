package com.macruware.fakestore.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentNotificationsContainerBinding

class NotificationsContainerFragment : Fragment() {
    private var _binding: FragmentNotificationsContainerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsContainerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}