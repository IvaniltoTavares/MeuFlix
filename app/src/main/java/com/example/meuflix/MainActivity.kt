package com.example.meuflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meuflix.adapter.FilmeAdapter
import com.example.meuflix.api.RetrofitService
import com.example.meuflix.databinding.ActivityMainBinding
import com.example.meuflix.model.filmespopulares.FilmeResposta
import com.example.meuflix.model.ultimosFilmes.FilmeLancamento
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private var paginaAtual = 1
    private var job: Job? = null
    private val TAG = "info_filme"
    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }

    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }
    var jobfilmeRecente : Job? = null
    var filmeAdapter: FilmeAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        filmeAdapter = FilmeAdapter{ filme ->
            val intent= Intent(this,DetalhesActivity::class.java)
            intent.putExtra("filme",filme)
            startActivity(intent)

        }

        binding.rvPopulares.adapter = filmeAdapter

        linearLayoutManager = GridLayoutManager(this,2
        )
        binding.rvPopulares.layoutManager = linearLayoutManager

        binding.rvPopulares.addOnScrollListener( object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                /*super.onScrolled(recyclerView, dx, dy)
                0 1 2...19(último item)

                val totalItens = recyclerView.adapter?.itemCount//20
                val ultimoItem = linearLayoutManager?.findLastVisibleItemPosition()
                //Log.i("onScrolled", "onScrolled: T: $totalItens U: $ultimoItem")
                if ( totalItens != null && ultimoItem != null ){
                    Log.i("onScrolled", "onScrolled: T: ${totalItens-1} U: $ultimoItem")
                    if( totalItens - 1 == ultimoItem ){
                        binding.fabAdicionar.hide()
                    }else{
                        binding.fabAdicionar.show()
                    }
                }

                Log.i("onScrolled", "onScrolled: dx: $dx dy: $dy")
                if( dy > 0 ){//Descendo até o último item (número sempre positivo)
                    binding.fabAdicionar.hide()
                }else{//Subindo até o primeiro item (número sempre net
                    binding.fabAdicionar.show()
                }*/

                //1 quando cehgar no final, pode
                val podeRolarVerticalmente = recyclerView.canScrollVertically(1)

                if (!podeRolarVerticalmente){
                    //carregar proxima pagina
                    recuperarFilmesProximaPagina()

//                    Log.i(TAG, "onScrolled: $podeRolarVerticalmente")
                }
            }
        })

        binding.btnPesquisa.setOnClickListener {
            val intent = Intent(this, PesquisaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun carregarImagemCapa() {

        CoroutineScope(Dispatchers.Main).launch {
            imagemCapa()
        }

    }

    private suspend fun imagemCapa() {

        jobfilmeRecente = CoroutineScope(Dispatchers.IO).launch{

            var resposta: Response<FilmeLancamento>? = null

            try {
                resposta = filmeAPI.recuperarFilmeLancamento()
                Log.i(TAG, "Sucesso ao carregar ultimos filmes adicionados")
                Log.i(TAG, "titulo do filme lancamento ${resposta.body()?.title} ")

            }catch (e:Exception){
                e.printStackTrace()
                Log.i(TAG, "erro ao recuperar filme lancamento: ")
            }
            if (resposta?.body()?.poster_path!= null) {
                if (resposta.isSuccessful) {
                    val filmeRecente = resposta.body()

                    val imagem = filmeRecente?.poster_path
                    val tamanhoImagem = "w780"
                    val url_base = RetrofitService.BASE_URL_IMAGE

                    val url = url_base + tamanhoImagem + imagem

                    withContext(Dispatchers.Main){

                        Picasso.get()
                            .load(url)
                            .into(binding.imgCapa)


                        binding.textViewTitulo.text = filmeRecente?.title
                    }

                } else {

                    Log.i(TAG, "Erro na requisição  -> codigo do erro: ")
                }
            }

        }

    }

    private fun recuperarFilmesProximaPagina(){
        if(paginaAtual < 1000){
            paginaAtual++
            recuperarFilmesPopulares(paginaAtual)

        }
    }

    private  fun recuperarFilmesPopulares(pagina:Int = 1){

        job = CoroutineScope(Dispatchers.IO).launch {

            var resposta : Response<FilmeResposta>? = null

            try {
                Log.i(TAG, "pagina atual : $paginaAtual: ")
                resposta = filmeAPI.recuperarFilmesPopulares(pagina)
            }catch (e:Exception){
                e.printStackTrace()
                Log.i(TAG, "Erro ao recuperar filmes populares: ")
            }

            if (resposta!= null){
                if (resposta.isSuccessful){

                    val listaFilmes = resposta.body()?.filme
                    if (listaFilmes!=null){
                        withContext(Dispatchers.Main){
                            filmeAdapter?.adicionarLista(listaFilmes)
                        }
                    }
                    /*listaFilme?.forEach {filme ->
                        Log.i(TAG, "${filme.id} - ${filme.title}")
                    }*/
                }else{
                    Log.i(TAG, "Erro na requisição  -> codigo do erro: ${resposta.code()} ")
                }

            }

        }

    }



    override fun onStart() {
        super.onStart()
        carregarImagemCapa()
        recuperarFilmesPopulares(1)

    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }
}