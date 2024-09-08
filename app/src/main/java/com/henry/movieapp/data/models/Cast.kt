package com.henry.movieapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Cast(var PicUrl: String = "", var Actor: String = "") : Parcelable {

    // Maybe you don't need this constructor with no arguments, but Firebase requires it, so i added this one below
    constructor() : this("", "")
}