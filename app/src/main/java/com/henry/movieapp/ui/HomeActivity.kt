package com.henry.movieapp.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.contains
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.henry.movieapp.R
import com.henry.movieapp.data.adapters.FilmAdapter
import com.henry.movieapp.data.adapters.SliderAdapter
import com.henry.movieapp.data.models.Film
import com.henry.movieapp.data.models.SliderItem
import com.henry.movieapp.databinding.ActivityHomeBinding
import com.henry.movieapp.databinding.ActivityLoginBinding
import com.henry.movieapp.utils.FIREBASE_URL
import com.henry.movieapp.utils.displayToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var database: FirebaseDatabase
    private var viewPagerJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Khi và chỉ khi khu vực quản lý cơ sở dữ liệu trong Firebase console là "United States" thì dùng:
        // database = FirebaseDatabase.getInstance()

        database = FirebaseDatabase.getInstance(FIREBASE_URL)

        initBanners()
        initTopMovies()
        initUpcomingMovies()
        configureUserInformation()
        configureNavigationBar()

        binding.simpleProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java).apply {
                putExtra("email", Firebase.auth.currentUser!!.email)
                putExtra("displayName", Firebase.auth.currentUser!!.displayName)
                putExtra("avatar", Firebase.auth.currentUser!!.photoUrl.toString())
            }
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            binding.homeLayout2.visibility = View.VISIBLE
            logout()
        }
    }

    override fun onPause() {
        super.onPause()
        viewPagerJob?.cancel()
    }

    override fun onResume() {
        super.onResume()

        viewPagerJob = startAutoSlide()
        binding.navBar.setItemSelected(R.id.explorer)
    }

    private fun initBanners() {
        val ref = database.getReference("Banners")
        val items = mutableListOf<SliderItem>()

        binding.progressBar1.visibility = View.VISIBLE
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        ds.getValue(SliderItem::class.java)?.let {
                            items.add(it)
                        }
                    }
                    getBanners(items)
                    binding.progressBar1.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.details)
            }
        })
    }

    private fun initTopMovies() {
        val ref = database.getReference("Items")
        val items = mutableListOf<Film>()

        binding.progressBar2.visibility = View.VISIBLE
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        ds.getValue(Film::class.java)?.let {
                            items.add(it)
                        }
                    }

                    if (items.isNotEmpty()) {
                        binding.recyclerViewTop.layoutManager = LinearLayoutManager(
                            this@HomeActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recyclerViewTop.adapter = FilmAdapter(items)
                    }

                    binding.progressBar2.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.details)
            }
        })
    }

    private fun initUpcomingMovies() {
        val ref = database.getReference("Upcomming")
        val items = mutableListOf<Film>()

        binding.progressBar3.visibility = View.VISIBLE
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        ds.getValue(Film::class.java)?.let { 
                            items.add(it)
                        }
                    }

                    if (items.isNotEmpty()) {
                        binding.recyclerViewUpcoming.layoutManager = LinearLayoutManager(
                            this@HomeActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.recyclerViewUpcoming.adapter = FilmAdapter(items)
                    }

                    binding.progressBar3.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.details)
            }
        })
    }

    private fun getBanners(items: MutableList<SliderItem>) {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }

        binding.viewPager2.adapter = SliderAdapter(items, binding.viewPager2)
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        binding.viewPager2.setPageTransformer(transformer)
        binding.viewPager2.currentItem = 1
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPagerJob?.cancel()
                viewPagerJob = startAutoSlide()
            }
        })
    }

    private fun startAutoSlide() = CoroutineScope(Dispatchers.Main).launch {
        while (isActive) {
            delay(3000)
            val nextItem =
                (binding.viewPager2.currentItem + 1) % binding.viewPager2.adapter!!.itemCount
            binding.viewPager2.currentItem = nextItem
        }
    }

    private fun configureUserInformation() {
        var option = RequestOptions()
        val email = intent.getStringExtra("email")
        val displayName = intent.getStringExtra("displayName")
        val avatar = intent.getStringExtra("avatar")

        option = option.transform(CenterCrop(), RoundedCorners(50))

        binding.emailTxt.text = email
        binding.displayNameTxt.text = displayName
        avatar?.let {
            Glide.with(this)
                .load(it)
                .override(100, 100)
                .apply(option)
                .into(binding.avatarImg)
        }
    }

    private fun configureNavigationBar() {
        val navBar = binding.navBar

        navBar.post {
            val id = navBar.getChildAt(0).id
            navBar.setItemSelected(id)
        }

        navBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.setting -> {
                    navBar.setItemSelected(id)
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
        }
    }

    private fun logout() {
        Firebase.auth.signOut()
        displayToast(this, "Logout successfully!\nReturning back to login page...", Toast.LENGTH_SHORT)
        binding.homeLayout2.visibility = View.GONE
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}