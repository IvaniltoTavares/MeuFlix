package com.example.meuflix.model.filmespopulares

import com.google.gson.annotations.SerializedName

data class FilmeResposta(
    val page: Int,
    @SerializedName("results")
    val filme: List<Filme>,
    val total_pages: Int,
    val total_results: Int
)