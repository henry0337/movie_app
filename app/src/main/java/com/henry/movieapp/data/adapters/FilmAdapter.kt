package com.henry.movieapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.henry.movieapp.R
import com.henry.movieapp.data.models.Film

class FilmAdapter(
    private var items: MutableList<Film>
) : RecyclerView.Adapter<FilmAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.film_viewholder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmAdapter.ViewHolder, position: Int) {
        var option = RequestOptions()
        option = option.transform(CenterCrop(), RoundedCorners(30))

        Glide.with(context)
            .load(items[position].Poster)
            .apply(option)
            .into(holder.pic)

        holder.titleTxt.text = items[position].Title
        holder.itemView.setOnClickListener {
            // TODO: Event will be added soon.
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTxt: TextView = view.findViewById(R.id.filmNameTxt)
        val pic: ImageView = view.findViewById(R.id.imageView3)
    }

}