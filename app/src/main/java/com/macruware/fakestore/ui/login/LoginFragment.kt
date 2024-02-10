package com.macruware.fakestore.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }

    private var username = ""
    private var password = ""
    private val minPasswordLength = 6
    private var isShowPassword = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)
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
                binding.btnShowPassword.setImageResource(R.drawable.ic_hide_password)
                binding.etPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.btnShowPassword.setImageResource(R.drawable.ic_show_password)
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            // Mover el cursor al final del texto
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
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

        // btn Login
        binding.btnLogin.setOnClickListener {
            // Validar campos
            if(username.isNotEmpty() && password.isNotEmpty()){
                stateCredentialsSuccess()
            } else {
                stateCredentialsError()
            }
        }
    }

    private fun stateCredentialsSuccess() {
        // call to api with credentials
        Toast.makeText(requireActivity(), "Calling to API...", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.VISIBLE
        usernameSuccess()
        passwordSuccess()
    }

    private fun stateCredentialsError() {
        Toast.makeText(requireActivity(), "Usuario o contraseña incorrectos. Intente nuevamente.", Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
        usernameError()
        passwordError()
    }

    private fun usernameSuccess(){
        binding.tvUsernameError.visibility = View.GONE
        binding.etUsername.backgroundTintList = requireContext().getColorStateList(R.color.white)
    }

    private fun usernameError() {
        binding.tvUsernameError.visibility = View.VISIBLE
        binding.etUsername.backgroundTintList = requireContext().getColorStateList(R.color.errorColor)
    }

    private fun passwordSuccess(){
        binding.tvPasswordError.visibility = View.GONE
        binding.etPassword.backgroundTintList = requireContext().getColorStateList(R.color.white)
    }

    private fun passwordError() {
        binding.tvPasswordError.visibility = View.VISIBLE
        binding.etPassword.backgroundTintList = requireContext().getColorStateList(R.color.errorColor)
    }

    private fun initUI() {
        fieldRestrictions()
    }

    private fun fieldRestrictions() {
        // Validate User Name
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString().trim()

                val regex = Regex("^[A-Za-záéíóúüñÁÉÍÓÚÜÑ\\s']+$")
                val isValidName = regex.matches(userInput)

                if (!isValidName && userInput.isNotEmpty()) {
                    binding.tvUsernameError.visibility = View.VISIBLE
                    username = ""
                } else {
                    binding.tvUsernameError.visibility = View.GONE
                    binding.etUsername.backgroundTintList = requireContext().getColorStateList(R.color.white)
                    username = userInput
                }

            }
            override fun afterTextChanged(p0: Editable?) {}
        })


        // Filtro para eliminar espacios en blanco dentro del password
        val inputFilter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {
                if (Character.isWhitespace(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }

        // Validate Password
        binding.etPassword.filters = arrayOf(inputFilter)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()

                if (userInput.isNotEmpty()){
                    if (userInput.length < minPasswordLength) {
                        binding.tvPasswordError.visibility = View.VISIBLE
                        password = ""
                    } else {
                        binding.tvPasswordError.visibility = View.GONE
                        binding.etPassword.backgroundTintList = requireContext().getColorStateList(R.color.white)
                        password = userInput
                    }
                    binding.etPassword.backgroundTintList = requireContext().getColorStateList(R.color.white)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun onBtnBackPressed(){
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog
            .setTitle("Fake Store")
            .setMessage("¿Deseas salir de la aplicación?")

        alertDialog.setPositiveButton("Salir") { _, _ ->
            requireActivity().finish()
        }
        alertDialog.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}