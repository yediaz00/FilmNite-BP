package com.example.filmnitebp.model

class Sala(){
    data class InfoSala(
        val estado:String,
        val id:String,
        val nombre:String
    )
    data class Stream(
        val nombre: String,
        val url:String
    )
    data class Tiempo(
        val absoluto: Float,
        val transcurido:Float
    )


}
