package com.macruware.fakestore.ui.register

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.macruware.fakestore.R
import com.macruware.fakestore.databinding.FragmentRegisterBinding
import com.macruware.fakestore.ui.main.MainActivity
import com.macruware.fakestore.ui.register.RegisterState.Error
import com.macruware.fakestore.ui.register.RegisterState.Loading
import com.macruware.fakestore.ui.register.RegisterState.Success

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

    private var username = ""
    private var userEmail = ""
    private var userPassword = ""

    private var isLoading = false

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

    private fun initUI() {
        fieldRestrictions()
        enableRegister()
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
           stateCredentialsSuccess()
        }
    }

    private fun stateCredentialsSuccess() {

        val apiResponse : RegisterState
        apiResponse = Success(username)

        isLoading = apiResponse == Loading
        when(apiResponse){
            Loading -> apiStateLoading()
            is Success -> apiStateSuccess(apiResponse)
            is Error -> apiStateError(apiResponse)
        }
    }

    private fun apiStateLoading() {
        binding.progressBar.visibility = View.VISIBLE
        disableListeners()
    }

    private fun apiStateSuccess(state: Success) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireActivity(), "Nuevo usuario registrado. ${state.newUser}", Toast.LENGTH_SHORT).show()
        goToMain()
    }

    private fun apiStateError(state: Error) {
        binding.progressBar.visibility = View.GONE
        Toast.makeText(requireActivity(), state.error, Toast.LENGTH_SHORT).show()
        enableListeners()
    }

    private fun enableListeners() {
        binding.etUsername.isEnabled = true
        binding.etEmail.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.btnShowPassword.isEnabled = true
        binding.etConfirmPassword.isEnabled = true
        binding.btnShowPasswordConfirm.isEnabled = true
        binding.btnGoToLogin.isEnabled = true
        binding.btnRegister.isEnabled = true
    }

    private fun disableListeners() {
        binding.etUsername.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.btnShowPassword.isEnabled = false
        binding.etConfirmPassword.isEnabled = false
        binding.btnShowPasswordConfirm.isEnabled = false
        binding.btnGoToLogin.isEnabled = false
        binding.btnRegister.isEnabled = false
    }

    // Se llama cada vez que se actualice si es válido o no
    private fun enableRegister(
        username: Boolean = isValidUsername,
        email: Boolean = isValidEmail,
        pass: Boolean = isValidPassword
    ) { // Toma por defecto el valor que ya tenga asignado

        // Si hay un nuevo valor, se actualiza y se mantienen los demás
        isValidUsername = username
        isValidEmail = email
        isValidPassword = pass

        // Activar botón de registrar solo si todos los campos son válidos
        if(isValidUsername && isValidEmail && isValidPassword){
            binding.btnRegister.isEnabled = true
            binding.btnRegister.setBackgroundColor(requireContext().getColor(R.color.secondaryColor))
        } else {
            binding.btnRegister.isEnabled = false
            binding.btnRegister.setBackgroundColor(requireContext().getColor(R.color.darkerGray))
        }
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
                    enableRegister(username = false)
                } else {
                    binding.tvUsernameError.visibility = View.GONE
                    username = userInput
                    enableRegister(username = true)
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
                        enableRegister(email = true)
                    } else {
                        binding.tvEmailError.visibility = View.VISIBLE
                        userEmail = userInput
                        enableRegister(email = false)
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
        var confirmPassword = ""
        binding.etPassword.filters = arrayOf(inputFilter)
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = s.toString()

                if (userInput.isNotEmpty()) {
                    if (userInput.length < minPasswordLength) {
                        binding.tvPasswordError.visibility = View.VISIBLE
                    } else {
                        binding.tvPasswordError.visibility = View.GONE
                    }
                }

                password = userInput
                verifyPassword(password, confirmPassword)

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
                    if (userInput.length < minPasswordLength) {
                        binding.tvConfirmPasswordError.visibility = View.VISIBLE
                    } else {
                        binding.tvConfirmPasswordError.visibility = View.GONE
                    }
                }

                confirmPassword = userInput
                verifyPassword(password, confirmPassword)

            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun verifyPassword(password: String, confirmPassword: String) {
        if (confirmPassword.isNotEmpty()) {
            if (password.isNotEmpty()) {
                if (password == confirmPassword) {
                    if (password.length >= minPasswordLength && confirmPassword.length >= minPasswordLength){
                        enableRegister(pass = true)
                        userPassword = password
                        binding.tvPasswordError.visibility = View.GONE
                        binding.tvConfirmPasswordError.visibility = View.GONE
                    } else {
                        enableRegister(pass = false)
                        binding.tvConfirmPasswordError.visibility = View.VISIBLE
                    }
                } else {
                    enableRegister(pass = false)
                    binding.tvConfirmPasswordError.visibility = View.VISIBLE
                }
            } else {
                enableRegister(pass = false)
                binding.tvPasswordError.visibility = View.VISIBLE
                binding.tvConfirmPasswordError.visibility = View.VISIBLE
            }
        } else {
            enableRegister(pass = false)
            binding.tvConfirmPasswordError.visibility = View.GONE
        }
    }

    private fun goToMain(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
    }

    private fun onBtnBackPressed() {
        if (isLoading){
            val alertDialog = AlertDialog.Builder(requireActivity())
            alertDialog
                .setTitle("Laza")
                .setMessage("¿Deseas salir de la aplicación?")

            alertDialog.setPositiveButton("Salir") { _, _ ->
                requireActivity().finish()
            }
            alertDialog.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        } else {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
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