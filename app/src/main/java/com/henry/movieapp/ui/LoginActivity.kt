package com.henry.movieapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.henry.movieapp.R
import com.henry.movieapp.databinding.ActivityLoginBinding
import com.henry.movieapp.utils.checkInternetStatus

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

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

        // Đoạn mã này không thể kiểm tra được ở trên máy ảo, bạn nên thử ở máy thật khi đến màn hình chứa đoạn mã này
        if (!checkInternetStatus(this)) {
            val builder = AlertDialog.Builder(this);

            builder
                .setTitle("Your device has no internet connection")
                .setMessage("Maybe you have turned off your Wifi or mobile cellular, or you connected to Wifi or mobile cellular but has no internet.\n You should double check this to ensure your experience in the app is not interrupted.")
                .setCancelable(true)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }

            builder.show()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = Firebase.auth
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            if (result.resultCode == Activity.RESULT_OK && data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            } else {
                Log.e("GoogleSignIn", "Google sign-in failed or cancelled")
            }
        }

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
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken)
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignInError", "Failed to sign in: ${e.localizedMessage}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, HomeActivity::class.java).apply {
                        putExtra("email", user?.email)
                        putExtra("displayName", user?.displayName)
                        putExtra("avatar", user?.photoUrl.toString())
                    }

                    startActivity(intent)
                    finish()
                } else {
                    Log.e("FirebaseAuthError", "Firebase authentication failed: ${task.exception?.message}")
                }
            }
    }
}
