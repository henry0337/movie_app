package com.henry.movieapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth
import com.henry.movieapp.R
import com.henry.movieapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        binding.apply {
            loginBtn.setOnClickListener {
                val email = emailEdt?.text.toString()
                val password = passwordEdt.text.toString()

                handlePasswordBasedAccountLogin(email, password, this)
            }

            ggBtn.setOnClickListener {
                handleGoogleAccountLogin()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun handlePasswordBasedAccountLogin(
        email: String = "",
        password: String = "",
        binding: ActivityLoginBinding
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.authLoading?.visibility = View.GONE
                    val user = auth.currentUser

                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("email", user?.email)
                        putExtra("displayName", user?.displayName)
                        putExtra("avatar", user?.photoUrl.toString())
                    }

                    startActivity(intent)
                    finish()
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            binding.authLoading?.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Your email or password is incorrect.\nYou can double check your typo then try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is FirebaseAuthInvalidUserException -> {
                            binding.authLoading?.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "Your account is not exist.\nMaybe your account had been disabled \nor deleted by the adminístrator of this app or someone else?",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                            binding.authLoading?.visibility = View.GONE
                            Toast.makeText(
                                this,
                                "We don't know what happened, please try again later.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

    }

    private fun handleGoogleAccountLogin() {

    }
}
