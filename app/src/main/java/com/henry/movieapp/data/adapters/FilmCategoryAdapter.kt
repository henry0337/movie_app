package com.henry.movieapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.henry.movieapp.R

class FilmCategoryAdapter(
    private var items: MutableList<String>
) : RecyclerView.Adapter<FilmCategoryAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmCategoryAdapter.ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.category_viewholder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FilmCategoryAdapter.ViewHolder, position: Int) {
        holder.titleTxt.text = items[position]
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTxt: TextView = view.findViewById(R.id.category_title)
    }
}