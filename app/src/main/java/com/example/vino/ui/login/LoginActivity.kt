package com.example.vino.ui.login

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.widget.addTextChangedListener
import com.example.vino.MainActivity
import com.example.vino.databinding.FragmentLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentLoginBinding
    private var density = 0F
    private var screenHeight = 0
    private val logoSizeHalf = 64

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        density = resources.displayMetrics.density
        screenHeight = resources.displayMetrics.heightPixels

        binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPasswordAndUsernameTextChangeListener()
        setSignInButtonClickListener()
        setCreateAccountClickListener()
    }

    override fun onRestart() {
        super.onRestart()
        // remove password from text box
        binding.passwordTextBox.setText("")
    }

    private fun setCreateAccountClickListener() {
        binding.createAccountButton.setOnClickListener {
            Toast.makeText(applicationContext, "Create an account will be available soon", Toast.LENGTH_LONG).show()
        }
    }

    private fun setSignInButtonClickListener() {
        binding.signInButton.setOnClickListener {
            if (isLoginValid(binding.passwordTextBox.text, binding.usernameTextBox.text)) {
                animateSignIn()
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

        return (passwordFlag != 1 && usernameFlag != 1)
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

    private fun setPasswordAndUsernameTextChangeListener() {
        binding.passwordTextBox.addTextChangedListener {
            // if password error, when user starts typing, remove error
            binding.passwordTextBoxLayout.error = null
        }

        binding.usernameTextBox.addTextChangedListener {
            // if password error, when user starts typing, remove error
            binding.usernameTextBoxLayout.error = null
        }
    }

    private fun animateSignIn() {
        val animationSet = AnimatorSet()
        animationSet.playTogether(
            getFadeViewAnimation(binding.usernameTextBox),
            getFadeViewAnimation(binding.usernameTextBoxLayout),
            getFadeViewAnimation(binding.passwordTextBox),
            getFadeViewAnimation(binding.passwordTextBoxLayout),
            getFadeViewAnimation(binding.signInButton),
            getFadeViewAnimation(binding.createAccountButton),
            getFadeViewAnimation(binding.appNameTitle)
        )

        val animationSet2 = AnimatorSet()
        animationSet2.playSequentially(
            animationSet,
            getLogoConstraintAnimation(32, (((screenHeight/2)/density) - logoSizeHalf).toInt())
        )
        animationSet2.doOnEnd {
            setVisibilityGoneAll()

            // start the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        animationSet2.start()
    }

    private fun setVisibilityGoneAll() {
        setVisibilityGone(binding.usernameTextBox)
        setVisibilityGone(binding.usernameTextBoxLayout)
        setVisibilityGone(binding.passwordTextBox)
        setVisibilityGone(binding.passwordTextBoxLayout)
        setVisibilityGone(binding.signInButton)
        setVisibilityGone(binding.createAccountButton)
        setVisibilityGone(binding.appNameTitle)
    }
    private fun setVisibilityGone(view: View) {
        view.visibility = View.GONE
    }

    private fun getLogoConstraintAnimation(start: Int, end: Int): ValueAnimator? {
        val mainConstraintLayout: ConstraintLayout = binding.loginConstraintLayout
        val constraintSet = ConstraintSet()

        val anim = ValueAnimator.ofInt(start, end)
        anim.addUpdateListener { valueAnimator ->
            // Set the corresponding property in the target view
            val newValue = valueAnimator.animatedValue as Int
            constraintSet.clone(mainConstraintLayout)
            constraintSet.connect(
                binding.logo.id,
                ConstraintSet.TOP,
                binding.loginConstraintLayout.id,
                ConstraintSet.TOP,
                (newValue * resources.displayMetrics.density).toInt()
            )
            constraintSet.applyTo(mainConstraintLayout)
        }
        anim.duration = 400
        return anim
    }

    private fun getFadeViewAnimation(view: View): ValueAnimator? {
        val anim = ValueAnimator.ofFloat(1f, 0f)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            view.alpha = value
        }
        anim.duration = 200
        return anim
    }
}