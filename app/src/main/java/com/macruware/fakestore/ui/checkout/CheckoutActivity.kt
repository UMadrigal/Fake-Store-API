package com.macruware.fakestore.ui.checkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.macruware.fakestore.databinding.ActivityCheckoutBinding

class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}