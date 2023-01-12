package com.example.meuflix.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val requisicaoAtual = chain.request().newBuilder()

        /*" https://api.themoviedb.org/3/ +  movie/popular?/"*/
        val urlAtual = chain.request().url()
        val novaUrl = urlAtual.newBuilder()
            .addQueryParameter("api_key", RetrofitService.API_KEY)
            /* .addQueryParameter("language", RetrofitService.LANGUAGE)*/
            .build()

        val novaRequisicao = requisicaoAtual.url(novaUrl)

        return chain.proceed(novaRequisicao.build())
    }
}