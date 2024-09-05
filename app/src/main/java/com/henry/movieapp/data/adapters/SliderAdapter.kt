package com.henry.movieapp.data.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.henry.movieapp.R
import com.henry.movieapp.data.models.SliderItem

class SliderAdapter(
    private val items: MutableList<SliderItem>,
    private val viewPager2: ViewPager2
) : RecyclerView.Adapter<SliderAdapter.ViewHolder>() {
    private lateinit var context: Context

    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable {
        items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.slider_viewholder, parent, false)
        )
    }


    override fun onBindViewHolder(holder: SliderAdapter.ViewHolder, position: Int) {
        holder.setImage(items[position])
        if (position == items.size - 2) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imageSlide)
        private val nameTxt: TextView = view.findViewById(R.id.nameTxt)
        private val genreTxt: TextView = view.findViewById(R.id.genreTxt)
        private val ageTxt: TextView = view.findViewById(R.id.ageTxt)
        private val yearTxt: TextView = view.findViewById(R.id.yearTxt)
        private val timeTxt: TextView = view.findViewById(R.id.hourTxt)

        @SuppressLint("SetTextI18n")
        fun setImage(item: SliderItem) {
            Glide.with(context)
                .load(item.image)
                .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(60)))
                .into(imageView)

            nameTxt.text = item.name
            genreTxt.text = item.genre
            ageTxt.text = item.age
            yearTxt.text = " ${item.year}"
            timeTxt.text = item.time
        }
    }
}