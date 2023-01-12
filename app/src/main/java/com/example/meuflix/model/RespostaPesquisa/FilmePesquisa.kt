package com.example.meuflix.model.RespostaPesquisa

import com.google.gson.annotations.SerializedName

data class FilmePesquisa(
    val page: Int,
    @SerializedName("results")
    val filme: List<Filme>,
    val total_pages: Int,
    val total_results: Int
)