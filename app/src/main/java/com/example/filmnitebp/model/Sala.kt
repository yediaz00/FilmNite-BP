package com.example.filmnitebp.model

data class SalaStream(
    val nombre: String,
    val url: String
)
data class SalaTiempo(
    val absoluto: Float,
    val transcurido:Float
)

data class Sala(
    val estado: String,
    val id: String,
    val nombre: String,
    val stream : SalaStream,
    val tiempo : SalaTiempo
)
