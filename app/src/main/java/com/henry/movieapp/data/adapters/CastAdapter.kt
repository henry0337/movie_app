package com.henry.movieapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.henry.movieapp.R
import com.henry.movieapp.data.models.Cast

class CastAdapter(
    private var actors: MutableList<Cast>
) : RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.actor_viewholder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CastAdapter.ViewHolder, position: Int) {
        Glide.with(context)
            .load(actors[position].PicUrl)
            .into(holder.pic)

        holder.nameTxt.text = actors[position].Actor
    }

    override fun getItemCount() = actors.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pic: ImageView = view.findViewById(R.id.actorImg)
        val nameTxt: TextView = view.findViewById(R.id.actorName)
    }
}