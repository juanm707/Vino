package com.example.vino.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.vino.MainActivity
import com.example.vino.databinding.FragmentLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPasswordTextChangeListener()
        setSignInButtonClickListener()
        setCreateAccountClickListener()
    }

    private fun setCreateAccountClickListener() {
        binding.createAccountButton.setOnClickListener {
            Toast.makeText(applicationContext, "Create an account will be available soon", Toast.LENGTH_LONG).show()
        }
    }

    private fun setSignInButtonClickListener() {
        binding.signInButton.setOnClickListener {
            if (isLoginValid(binding.passwordTextBox.text, binding.usernameTextBox.text)) {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent)
            }
        }
    }

    private fun isLoginValid(password: Editable?, username: Editable?): Boolean {
        var usernameFlag = 0
        var passwordFlag = 0

        if (!isUsernameValid(username))
            usernameFlag = 1

        if (!isPasswordValid(password))
            passwordFlag = 1

        return (passwordFlag != 1 || usernameFlag != 1)
    }

    private fun isPasswordValid(password: Editable?): Boolean {
        if (password != null) {
            return if (password.isNullOrEmpty() || password.isNullOrBlank() || password.length < 8) {
                binding.passwordTextBoxLayout.error = "Password must contain at least 8 characters."
                false
            } else {
                binding.passwordTextBoxLayout.error = null
                true
            }
        }
        return false
    }

    private fun isUsernameValid(username: Editable?): Boolean {
        return if (username.isNullOrEmpty() || username.isNullOrBlank()) {
            // need username, show error
            binding.usernameTextBoxLayout.error = "Username is required."
            false
        } else {
            binding.usernameTextBoxLayout.error = null
            true
        }
    }

    private fun setPasswordTextChangeListener() {
        binding.passwordTextBox.addTextChangedListener {
            // if password error, when user starts typing, remove error
            binding.passwordTextBoxLayout.error = null
        }
    }
}