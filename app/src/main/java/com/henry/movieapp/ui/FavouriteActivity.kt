package com.henry.movieapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.henry.movieapp.R
import com.henry.movieapp.databinding.ActivityFavouriteBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class FavouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var navBar: ChipNavigationBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navBar = binding.navBar
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navBar.setItemSelected(R.id.explorer, true)

                if (isEnabled) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        configureNavigationBar()
    }

    override fun onStart() {
        super.onStart()

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navBar.setOnItemSelectedListener { id ->
                when (id) {
                    R.id.explorer -> {
                        navBar.setItemSelected(id)
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("email", currentUser.email)
                            putExtra("displayName", currentUser.displayName)
                            putExtra("avatar", currentUser.photoUrl.toString())
                        }

                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun configureNavigationBar() {
        navBar.post {
            val id = navBar.getChildAt(1).id
            navBar.setItemSelected(id)
        }

        navBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.explorer -> {
                    navBar.setItemSelected(id)
                    startActivity(Intent(this, HomeActivity::class.java))
                }
            }
        }
    }
}