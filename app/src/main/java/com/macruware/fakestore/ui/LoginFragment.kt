package com.macruware.fakestore.ui

import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var isShowPassword = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initUI()
    }

    private fun initListeners() {
        binding.btnShowPassword.setOnClickListener {
            isShowPassword = !isShowPassword
            if (isShowPassword) {
                binding.btnShowPassword.setImageResource(R.drawable.ic_show_password_true)
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.btnShowPassword.setImageResource(R.drawable.ic_show_password_false)
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            // Mover el cursor al final del texto
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnGoToRegister.setOnClickListener {
//            findNavControllar navigate
            Toast.makeText(requireActivity(),"Ir a registro", Toast.LENGTH_SHORT).show()
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                // Ocultar teclado
                val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etPassword.windowToken, 0)

                // Ocultar el cursor y quitar el foco del EditText
                binding.etPassword.clearFocus()

                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun initUI() {

    }
}