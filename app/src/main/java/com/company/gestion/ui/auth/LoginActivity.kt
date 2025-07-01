package com.company.gestion.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.company.gestion.MainActivity
import com.company.gestion.R
import com.company.gestion.data.repository.AuthRepository
import com.company.gestion.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Verificar si el usuario ya está autenticado
        if (authRepository.isUserSignedIn()) {
            navigateToMain()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signIn(email, password)
            }
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                signUp(email, password)
            }
        }

        // Pre-llenar con tu email para facilitar el testing
        binding.etEmail.setText("angelb042512@gmail.com")
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email requerido"
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Contraseña requerida"
            return false
        }
        if (password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        return true
    }

    private fun signIn(email: String, password: String) {
        binding.btnLogin.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            authRepository.signIn(email, password)
                .onSuccess {
                    Toast.makeText(this@LoginActivity, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                .onFailure { exception ->
                    Toast.makeText(this@LoginActivity, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    binding.btnLogin.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                }
        }
    }

    private fun signUp(email: String, password: String) {
        binding.btnRegister.isEnabled = false
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            authRepository.signUp(email, password)
                .onSuccess {
                    Toast.makeText(this@LoginActivity, "Usuario creado exitosamente!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                .onFailure { exception ->
                    Toast.makeText(this@LoginActivity, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                    binding.btnRegister.isEnabled = true
                    binding.progressBar.visibility = android.view.View.GONE
                }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
