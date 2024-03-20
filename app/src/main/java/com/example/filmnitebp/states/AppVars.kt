package com.example.filmnitebp.states

import com.example.filmnitebp.model.SalaStream
import io.ktor.http.URLBuilder
import io.ktor.http.Url

class AppVars {
    companion object {
        var url = ""
        lateinit var streamItem: SalaStream
        val BASE_URL = URLBuilder("http://piola.cloudns.nz:12012")
    }
}