package com.example.meuflix

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.example.meuflix.api.RetrofitService
import com.example.meuflix.databinding.ActivityDetalhesBinding
import com.example.meuflix.model.DetalhesFilme.RespostaDetalhes
import com.example.meuflix.model.filmespopulares.Filme
import com.example.meuflix.model.videos.VideoResposta
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class DetalhesActivity : AppCompatActivity() {
    val TAG = "chave"
    var filme: Filme? = null
    private var job: Job? = null

    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }

    private val binding by lazy {
        ActivityDetalhesBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var video: VideoResposta? = null
        val filmeId = filme?.id
        Log.i("info_trailer", "id filme no oncreat: ${filmeId}")

        var filme: Filme? = null
        val bundle = intent.extras
        if (bundle != null) {
            filme = bundle.getSerializable("filme") as Filme // abastece as classes com a requisição

            val nomeImagem = filme.poster_path
            val tamanhoImagem = "w780"
            val url_base = RetrofitService.BASE_URL_IMAGE
            val url = url_base + tamanhoImagem + nomeImagem

            Picasso.get()
                .load(url)
                //.placeholder()
                .into(binding.imgPoster)

            binding.textTituloDescricao.text = filme.title

            binding.textDescricaoDetalhes.text = filme.overview

        }

        verificar()

        binding.btnTrailer.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                recuperarDetalhesVideos()

            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun verificar(){
        CoroutineScope(Dispatchers.IO).launch {
            recuperarDetalheFilme()
            verificarTrailer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun recuperarDetalheFilme(){
        var Detalhe: RespostaDetalhes? = null


        val bundle = intent.extras
        if (bundle != null) {
            filme = bundle.getSerializable("filme") as Filme
        }
        var retorno: Response<RespostaDetalhes>? = null

        val idFilme = filme?.id
        Log.i("info_trailer", "ID do filme-> ${idFilme}  ")

        try {
            retorno = filmeAPI.recuperarDetalhes(idFilme)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("info_trailer", "Erro ao recuperar detalhes trailer: ")
        }

        if (retorno != null) {
            if (retorno.isSuccessful) {

                Log.i("info_trailer", "Sucesso ")

                val videoDetalhes = retorno.body()
                val genero = videoDetalhes?.genres?.get(0)?.name
                var ano = videoDetalhes?.release_date

                val formatoEntrada = SimpleDateFormat("yyyy-MM-dd")
                val formatoSaida = SimpleDateFormat("dd-MM-yyyy")
                val dataEntrada = formatoEntrada.parse(ano)
                val dataPortugues = formatoSaida.format(dataEntrada)



                binding.textGenero.text = genero
                binding.editData.text = dataPortugues
            }
        }

    }

    private suspend fun verificarTrailer() {
        var filme: Filme? = null

        val bundle = intent.extras
        if (bundle != null) {
            filme = bundle.getSerializable("filme") as Filme
        }
        var retorno: Response<VideoResposta>? = null

        val idFilme = filme?.id
        Log.i("info_trailer", "ID do filme-> ${idFilme}  ")

        try {

            retorno = filmeAPI.recuperarVideos(idFilme)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("info_trailer", "Erro ao recuperar detalhes trailer: ")
        }

        if (retorno != null) {
            if (retorno.isSuccessful) {

                Log.i("info_trailer", "Sucesso ")

                val videoDetalhes = retorno.body()
                val id = videoDetalhes?.id
                /*Log.i("chave_video", "video detalhes: $videoDetalhes")*/
                val dadosFilme = videoDetalhes?.results
                if (dadosFilme != null && dadosFilme.isNotEmpty()) {
                    val chaveVideo = dadosFilme.get(0).key

                    Log.i("info_trailer", "ID ->${id} ")
                    Log.i("info_trailer", "Chave do you tube  ${chaveVideo}")

                    if (chaveVideo != null) {
                        Log.i("chave_video", "tem chave : $chaveVideo")
                        withContext(Dispatchers.Main){
                            binding.btnTrailer.visibility = View.VISIBLE
                            binding.textTrailer.visibility = View.VISIBLE
                        }
                    }
                }else{
                    Log.i("chave_video", "nao tem chave ???")

                }
            }
        }
    }

    private suspend fun recuperarDetalhesVideos() {

        var filme: Filme? = null

        val bundle = intent.extras
        if (bundle != null) {
            filme = bundle.getSerializable("filme") as Filme
        }
        var retorno: Response<VideoResposta>? = null

        val idFilme = filme?.id
        Log.i("info_trailer", "ID do filme-> ${idFilme}  ")

        try {

            retorno = filmeAPI.recuperarVideos(idFilme)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("info_trailer", "Erro ao recuperar detalhes trailer: ")
        }

        if (retorno != null) {
            if (retorno.isSuccessful) {

                Log.i("info_trailer", "Sucesso ")

                val videoDetalhes = retorno.body()
                val id = videoDetalhes?.id
                /*Log.i("chave_video", "video detalhes: $videoDetalhes")*/
                val dadosFilme = videoDetalhes?.results
                if (dadosFilme!=null && dadosFilme.isNotEmpty()){
                    val chaveVideo = dadosFilme.get(0).key

                    Log.i("info_trailer", "ID ->${id} ")
                    Log.i("info_trailer", "Chave do you tube  ${chaveVideo}")

                    if (chaveVideo!=null){
                        Log.i("chave_video", "tem chave : $chaveVideo")
                        val intent = Intent(this, TrailerActivity::class.java)
                        intent.putExtra("trailer", chaveVideo)
                        startActivity(intent)
                    }

                }else{
                    Log.i("chave_video", "nao tem chave : ")
                    withContext(Dispatchers.Main){

                        Toast.makeText(applicationContext, "falha ao carregar trailer", Toast.LENGTH_SHORT).show()

                    }

                }

                /*listaFilme?.forEach {filme ->
                Log.i(TAG, "${filme.id} - ${filme.title}")
                }*/
            } else {
                Log.i(
                    "info_trailer",
                    "Erro na requisição Detalhe Video  -> codigo do erro: ${retorno?.code()} "
                )
            }
        }
    }
}