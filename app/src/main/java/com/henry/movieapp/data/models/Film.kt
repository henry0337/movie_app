package com.henry.movieapp.data.models

class Film {
    var Title = ""
    var Description = ""
    var Poster = ""
    var Time = ""
    var Trailer = ""
    var Imdb = 0
    var year = 0
    var Genre = mutableListOf<String>()
    var Actors = mutableListOf<Cast>()
}