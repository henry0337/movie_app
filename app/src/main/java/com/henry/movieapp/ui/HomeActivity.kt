package com.henry.movieapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
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
import com.henry.movieapp.utils.FIREBASE_URL
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

        database = FirebaseDatabase.getInstance(FIREBASE_URL)

        initBanners()
        initTopMovies()
        initUpcomingMovies()
    }

    override fun onPause() {
        super.onPause()
        viewPagerJob?.cancel()
    }

    override fun onResume() {
        super.onResume()
        viewPagerJob = startAutoSlide()
    }

    private fun initBanners() {
        val ref = database.getReference("Banners")
        val items = mutableListOf<SliderItem>()

        binding.progressBar1.visibility = View.VISIBLE
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (ds in snapshot.children) {
                        items.add(ds.getValue(SliderItem::class.java)!!)
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
                        items.add(ds.getValue(Film::class.java)!!)
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
                        items.add(ds.getValue(Film::class.java)!!)
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
            delay(2000) // 2-second delay
            val nextItem = (binding.viewPager2.currentItem + 1) % binding.viewPager2.adapter!!.itemCount
            binding.viewPager2.currentItem = nextItem
        }
    }
}