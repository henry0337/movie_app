package com.henry.movieapp.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.henry.movieapp.R
import com.henry.movieapp.data.adapters.CastAdapter
import com.henry.movieapp.data.adapters.FilmCategoryAdapter
import com.henry.movieapp.data.models.Film
import com.henry.movieapp.databinding.ActivityDetailBinding
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setVariables()
    }

    @SuppressLint("SetTextI18n")
    private fun setVariables() {
        val radius = 10F
        var option = RequestOptions()
        val decorView = window.decorView
        val windowBackground = decorView.background
        val rootView: ViewGroup = decorView.findViewById(android.R.id.content)
        val item: Film? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("filmList", Film::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("filmList")
        }

        option = option.transform(CenterCrop(), GranularRoundedCorners(0F, 0F, 50F, 50F))

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(item!!.Poster)
                .apply(option)
                .into(filmImg)

            titleTxt.text = item.Title
            imdbTxt.text = "IMDB: ${item.Imdb}"
            movieTimeTxt.text = "${item.year} - ${item.Time}"
            movieSummary.text = item.Description

            watchBtn.setOnClickListener {
                // Lấy ra id từ video Youtube
                val id = item.Trailer.replace("https://www.youtube.com/watch?v=", "")
                // "vnd.youtube:{id}" là URI schema dùng để mở video trên ứng dụng Youtube ở Android
                val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.Trailer))

                try {
                    // Dùng ứng dụng Youtube để mở video nếu được
                    startActivity(appIntent)
                } catch (e: ActivityNotFoundException) {
                    // Nếu không được thì mở dùng trình duyệt mặc định để mở
                    Log.v(
                        "IntentException",
                        "Không thấy ứng dụng Youtube trên thiết bị, hệ thống sẽ tự động chuyển sang trình duyệt."
                    )
                    startActivity(webIntent)
                }
            }

            backImg.setOnClickListener {
                finish()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                blurView.setupWith(rootView, RenderEffectBlur())
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
            } else {
                @Suppress("DEPRECATION")
                blurView.setupWith(rootView, RenderScriptBlur(this@DetailActivity))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(radius)
            }
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true

            genreView.adapter = FilmCategoryAdapter(item.Genre)
            genreView.layoutManager =
                LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)

            castView.adapter = CastAdapter(item.Casts)
            castView.layoutManager =
                LinearLayoutManager(this@DetailActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }
}