package com.henry.movieapp.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import com.henry.movieapp.utils.displayToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        CoroutineScope(Dispatchers.IO).launch {
            if (!checkInternetStatus(this@LoginActivity)) {
                val builder = AlertDialog.Builder(this@LoginActivity)

                builder
                    .setTitle("Your device has no internet connection")
                    .setMessage("Maybe you have turned off your Wifi or mobile cellular, or you connected to Wifi or mobile cellular but has no internet.\n\nYou should double check this for your better experience.")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }


                builder.show()
            }
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
            emailEdt.setOnFocusChangeListener { view, hasFocused ->
                emailEdt.hint = if (hasFocused) "" else "Your email"
                if (!hasFocused) hideKeyboard(view)
            }

            passwordEdt.setOnFocusChangeListener { view, hasFocused ->
                passwordEdt.hint = if (hasFocused) "" else "Your password"
                if (!hasFocused) hideKeyboard(view)
            }

            if (signUpPage.isClickable) {
                signUpPage.setOnClickListener {
                    startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                }
            }

            if (forgotPasswordTxt.isClickable) {
                forgotPasswordTxt.setOnClickListener {
                    // TODO: Thử làm gì đó với nút bấm này
                }
            }

            loginBtn.setOnClickListener {
                val email = emailEdt.text.toString()
                val password = passwordEdt.text.toString()

                binding.authLoading.visibility = View.VISIBLE
                handlePasswordBasedAccountLoggingIn(email, password, this)
            }

            ggBtn.setOnClickListener {
                binding.authLoading.visibility = View.VISIBLE
                handleGoogleAccountLoggingIn()
            }
        }
    }

    // Hàm xử lý sự kiện khi người dùng chạm vào màn hình
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                // Nếu như khu vực mà người dùng click không nằm trong EditText...
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    // thì loại bỏ trạng thái focus
                    view.clearFocus()
                    // và ẩn bàn phím của thiết bị nếu đang hiện
                    hideKeyboard(view)
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    private fun handlePasswordBasedAccountLoggingIn(
        email: String = "",
        password: String = "",
        binding: ActivityLoginBinding
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.authLoading.visibility = View.GONE
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
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            displayToast(this, "Your email or password is incorrect.\nYou should double-check your typo and try again.", Toast.LENGTH_LONG)
                        }

                        is FirebaseAuthInvalidUserException -> {
                            displayToast(this, "Your account does not exist.\nMaybe your account had been disabled \nor deleted by the adminístrator of this app or someone else?", Toast.LENGTH_LONG)
                        }

                        else -> {
                            displayToast(this, "We don't know what happened, please try again later.", Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
    }

    private fun handleGoogleAccountLoggingIn() {
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
                    displayToast(this, "Welcome back, ${user?.displayName}!", Toast.LENGTH_LONG)
                    binding.authLoading.visibility = View.GONE
                    finish()
                } else {
                    Log.e("FirebaseAuthError", "Firebase authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
