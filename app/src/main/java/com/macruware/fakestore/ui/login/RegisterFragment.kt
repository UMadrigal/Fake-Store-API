package com.macruware.fakestore.ui.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBtnBackPressed()
        }
    }

    private var isShowPassword = false
    private var isShowPasswordConfirm = false
    private val minPasswordLength = 6

    private var isValidUsername = false
    private var isValidEmail = false
    private var isValidPassword = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initUI()
    }

    private fun initListeners() {
        // Go to Login
        binding.btnGoToLogin.setOnClickListener {
            onBtnBackPressed()
//            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Password
        binding.btnShowPassword.setOnClickListener {
            isShowPassword = !isShowPassword
            showPassword(isShowPassword, binding.btnShowPassword, binding.etPassword)
        }

        // Confirm Password
        binding.btnShowPasswordConfirm.setOnClickListener {
            isShowPasswordConfirm = !isShowPasswordConfirm
            showPassword(
                isShowPasswordConfirm,
                binding.btnShowPasswordConfirm,
                binding.etConfirmPassword
            )
        }

        // Ocultar teclado y quitar cursor
        binding.etConfirmPassword.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                // Ocultar teclado
                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etConfirmPassword.windowToken, 0)

                // Ocultar el cursor y quitar el foco del EditText
                binding.etConfirmPassword.clearFocus()

                return@setOnEditorActionListener true
            }
            false
        }

        binding.btnRegister.setOnClickListener {
            stateLoading()
            // Llamar a la API y recuperar state
        }
    }

    private fun stateLoading() {
        binding.progressBar.visibility = View.VISIBLE
        Toast.makeText(requireActivity(), "Registrando usuario...", Toast.LENGTH_SHORT).show()
    }

    private fun initUI() {
        fieldRestrictions()
        enableButton()
    }

    // Se llama cada vez que se actualice si es válido o no
    private fun enableButton(
        username: Boolean = isValidUsername,
        email: Boolean = isValidEmail,
        pass: Boolean = isValidPassword
    ) { // Toma por defecto el valor que ya tenga asignado

        // Si hay un nuevo valor, se actualiza y se mantienen los demás
        isValidUsername = username
        isValidEmail = email
        isValidPassword = pass

        // Activar botón de registrar solo si todos los campos son válidos
        binding.btnRegister.isEnabled = isValidUsername && isValidEmail && isValidPassword
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
//                    isValidUsername = false
                    enableButton(username = false)
                } else {
                    binding.tvUsernameError.visibility = View.GONE
//                    isValidUsername = true
                    enableButton(username = true)
                }

            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        // Validate Email
        val pattern = Patterns.EMAIL_ADDRESS
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()

                if (userInput.isNotEmpty()) {
                    if (pattern.matcher(userInput).matches()) {
                        binding.tvEmailError.visibility = View.GONE
//                            isValidEmail = true
                        enableButton(email = true)
                    } else {
                        binding.tvEmailError.visibility = View.VISIBLE
//                            isValidEmail = false
                        enableButton(email = false)
                    }
                } else {
                    binding.tvEmailError.visibility = View.GONE
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

        // Validate User Password
        var password = ""

        binding.etPassword.filters = arrayOf(inputFilter)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()

                if (userInput.isNotEmpty()) {
                    if (userInput.length < minPasswordLength) {
                        binding.tvPasswordError.visibility = View.VISIBLE
                        password = ""
                    } else {
                        binding.tvPasswordError.visibility = View.GONE
                        password = userInput
                    }
                }

            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        // Validate User Confirm Password
        binding.etConfirmPassword.filters = arrayOf(inputFilter)
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()

                if (userInput.isNotEmpty()) {
                    if (userInput.length < minPasswordLength || userInput != password) {
                        binding.tvConfirmPasswordError.visibility = View.VISIBLE
                        enableButton(pass = false)
                    } else {
                        binding.tvConfirmPasswordError.visibility = View.GONE
                        enableButton(pass = true)
                    }
                }


            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun onBtnBackPressed() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun showPassword(isShow: Boolean, button: ImageButton, editText: EditText) {
        // Si se ve la contraseña
        if (isShow) {
            // Mostrar botón de ocultar contraseña
            button.setImageResource(R.drawable.ic_hide_password)
            editText.transformationMethod =
                HideReturnsTransformationMethod.getInstance()
        } else { // Si no se ve la contraseña
            // Mostrar botón de ver contraseña
            button.setImageResource(R.drawable.ic_show_password)
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()
        }
        // Mover el cursor al final del texto
        editText.setSelection(editText.text?.length ?: 0)
    }
}