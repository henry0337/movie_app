package com.henry.movieapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.henry.movieapp.R
import com.henry.movieapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth

        binding.apply {
            registerBtn.setOnClickListener {
                val email = usernameEdt.text.toString()
                val password = passwordEdt.text.toString()

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this@RegisterActivity) { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(
                            binding.main,
                            "Register successfully!\nDo you want to be redirected to login page now?",
                            Snackbar.LENGTH_INDEFINITE
                        ).apply {
                            setAction("OK") {
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            }
                        }.show()
                    }
                }
            }

            loginPageBtn.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }
    }
}