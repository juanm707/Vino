package com.example.vino.ui.login

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.animation.doOnEnd
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_SOUND
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.addTextChangedListener
import com.example.vino.MainActivity
import com.example.vino.R
import com.example.vino.databinding.FragmentLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: FragmentLoginBinding
    private var density = 0F
    private var screenHeight = 0
    private val logoSizeHalf = 64

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //createNotificationChannel()

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
//                val builder = NotificationCompat.Builder(this, "vino_02")
//                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                    .setContentTitle("My notification")
//                    .setContentText("Much longer text that cannot fit one line...")
//                    .setStyle(NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE )
//                    .setAutoCancel(true)
//
//                val notificationId = 707
//                with(NotificationManagerCompat.from(this)) {
//                    // notificationId is a unique int for each notification that you must define
//                    notify(notificationId, builder.build())
//                }
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
            setVisibilityGoneAllLoginView()

            // start the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        animationSet2.start()
    }

    private fun setVisibilityGoneAllLoginView() {
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

//    private fun createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = "Vino Pepino"
//            val descriptionText = "Sample Channel description"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel("vino_02", name, importance).apply {
//                description = descriptionText
//            }
//            // Register the channel with the system
//            val notificationManager: NotificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
}