package com.henry.movieapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.henry.movieapp.R
import com.henry.movieapp.data.models.User
import com.henry.movieapp.databinding.ActivityAuthBinding

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var oneTapLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)

        oneTapLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            handleResult(result)
        }

        binding.ggBtn.setOnClickListener {
            onLogin()
        }
    }

    private fun onLogin() {
        val request = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(request)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent).build()
                oneTapLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { e ->
                Log.e("OneTapSignIn", "Failed to start sign-in: ${e.message}")
            }
    }

    private fun handleResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data = result.data
            try {
                val credential = oneTapClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {
                                    val bindingUser = User().apply {
                                        name = user.displayName!!
                                        avatar = user.photoUrl.toString()
                                        email = user.email!!
                                    }
                                    // Navigate to HomeActivity
                                    navigateToHome(bindingUser.name, bindingUser.email, bindingUser.avatar)
                                }
                            } else {
                                task.exception?.message?.let { Log.e("FirebaseAuthException", it) }
                            }
                        }
                } else {
                    Log.e("Google Sign-In", "No ID token!")
                }
            } catch (e: ApiException) {
                Log.e("ApiException", "Google Sign-In failed", e)
            }
        } else {
            Log.d("Sign-In", "Sign-In canceled or failed.")
        }
    }

    private fun navigateToHome(name: String?, email: String?, photoUrl: String?) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("displayName", name)
            putExtra("email", email)
            putExtra("photoUrl", photoUrl)
        }
        startActivity(intent)
        finish()
    }
}
