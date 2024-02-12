package com.macruware.fakestore.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}