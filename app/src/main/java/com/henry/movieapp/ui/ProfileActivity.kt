package com.henry.movieapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.henry.movieapp.R
import com.henry.movieapp.databinding.ActivityProfileBinding
import com.henry.movieapp.utils.displayToast

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            logoutBtn.setOnClickListener {
                logout()
            }
            returnBackBtn.setOnClickListener {
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val option = RequestOptions()
            .transform(CenterCrop(), RoundedCorners(30))
            .override(300, 300)

        binding.apply {
            Glide.with(this@ProfileActivity)
                .load(intent.getStringExtra("avatar"))
                .apply(option)
                .into(avatarImg)

            displayNameTxt.text = intent.getStringExtra("displayName")
            emailProfileTxt.text = intent.getStringExtra("email")
        }
    }

    private fun logout() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Log out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                Firebase.auth.signOut()
                displayToast(this, "Log out successfully!", Toast.LENGTH_SHORT)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }

        dialog.show()
    }
}