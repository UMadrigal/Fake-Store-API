package com.macruware.fakestore.ui.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}