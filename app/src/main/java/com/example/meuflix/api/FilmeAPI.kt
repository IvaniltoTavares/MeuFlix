package com.example.meuflix.api

import com.example.meuflix.model.DetalhesFilme.RespostaDetalhes
import com.example.meuflix.model.RespostaPesquisa.FilmePesquisa
import com.example.meuflix.model.filmespopulares.FilmeResposta
import com.example.meuflix.model.ultimosFilmes.FilmeLancamento
import com.example.meuflix.model.videos.VideoResposta
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmeAPI {

    @GET("movie/{movie_id}?api_key=${RetrofitService.API_KEY}&language=pt-BR")
    suspend fun recuperarDetalhes(
        @Path("movie_id") idFilme: Int?
    ): Response<RespostaDetalhes>


    @GET("movie/{movie_id}/videos?api_key=${RetrofitService.API_KEY}&language=pt-BR")
    suspend fun recuperarVideos(
        @Path("movie_id") idFilme: Int?
    ): Response<VideoResposta>


    @GET("movie/latest?api_key=${RetrofitService.API_KEY}&language=pt-BR")
    suspend fun recuperarFilmeLancamento(): Response<FilmeLancamento>


    /*@GET("movie/popular?api_key=${RetrofitService.API_KEY}&language=pt-BR")*/
    @GET("movie/popular?api_key=${RetrofitService.API_KEY}&language=pt-BR")
    suspend fun recuperarFilmesPopulares(
        /* @Query("language") linguagem: String = "pt-BR",*/
        @Query("page") pagina: Int = 1
    ): Response<FilmeResposta>

    @GET("search/movie?api_key=${RetrofitService.API_KEY}&language=pt-BR")
    suspend fun recuperarFilmesPesquisa(
        @Query("query") pesquisa:String,
        @Query("page") pagina: Int = 1
    ): Response<FilmeResposta>

}