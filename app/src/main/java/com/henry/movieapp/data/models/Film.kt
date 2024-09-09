package com.henry.movieapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Film(
    var Title: String = "", var Description: String = "", var Poster: String = "",
    var Time: String = "", var Trailer: String = "", var Imdb: Int = 0, var year: Int = 0,
    var Genre: MutableList<String> = mutableListOf(),
    var Casts: MutableList<Cast> = mutableListOf()
) : Parcelable {

    // Maybe you don't need this constructor with no arguments, but Firebase requires it, so i added this one below
    constructor() : this("", "", "", "", "", 0, 0, mutableListOf(), mutableListOf())
}