package com.example.meuflix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meuflix.adapter.PesquisaAdapter
import com.example.meuflix.api.RetrofitService
import com.example.meuflix.databinding.ActivityPesquisaBinding
import com.example.meuflix.model.RespostaPesquisa.FilmePesquisa
import com.example.meuflix.model.filmespopulares.Filme
import com.example.meuflix.model.filmespopulares.FilmeResposta
import kotlinx.coroutines.*
import retrofit2.Response

class PesquisaActivity : AppCompatActivity() {
    private var job: Job? = null
    private val TAG = "info_filme"
    private var paginaAtual = 1


    var pesquisaAdapter: PesquisaAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null



    private val binding by lazy {
        ActivityPesquisaBinding.inflate(layoutInflater)
    }

    private val filmeAPI by lazy {
        RetrofitService.filmeAPI
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        pesquisaAdapter = PesquisaAdapter { filme ->
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("filme", filme)
            startActivity(intent)
        }

        binding.rvPesquisa.adapter = pesquisaAdapter

        linearLayoutManager = GridLayoutManager(this,2
        )
        binding.rvPesquisa.layoutManager = linearLayoutManager

        binding.rvPesquisa.addOnScrollListener( object : RecyclerView.OnScrollListener(){
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
                    CoroutineScope(Dispatchers.Main).launch {
                        recuperarFilmesProximaPagina()
                    }
//                    Log.i(TAG, "onScrolled: $podeRolarVerticalmente")
                }
            }
        })

        binding.btnBuscar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {

                recuperarFilmesProximaPagina()
            }
        }
    }

    private suspend fun recuperarFilmesProximaPagina(){
        if(paginaAtual < 1000){
            paginaAtual++
            recuperarFilmesPesquisa(paginaAtual)

        }
    }

    private suspend fun recuperarFilmesPesquisa( pagina:Int = 1){

        job = CoroutineScope(Dispatchers.IO).launch {

            val pesquisa = binding.editTextPesquisa.text

            var retorno : Response<FilmeResposta>? = null

            try {
                retorno = filmeAPI.recuperarFilmesPesquisa("$pesquisa",pagina)
            }catch (e:Exception){
                e.printStackTrace()
                Log.i(TAG, "Erro ao recuperar filmes pesquisa: ")
            }

            /*val imagem = retorno?.body()?.filme?.get(1)?.backdrop_path*/


            if (retorno!= null ){
                if (retorno.isSuccessful){

                    val listaFilmes = retorno.body()?.filme

                    val listaFilmePesquisa = mutableListOf<Filme>()

                    /*if(listaFilmes != null){

                        withContext(Dispatchers.Main){
                            pesquisaAdapter?.adicionarListaPesquisa(listaFilmes)
                        }

                    }*/

                    listaFilmes?.forEach { filme ->
                        if (filme.backdrop_path!=null){
                            listaFilmePesquisa.add(filme)

                        }

                    }
                    if(listaFilmePesquisa != null){
                        withContext(Dispatchers.Main){
                            pesquisaAdapter?.adicionarLista(listaFilmePesquisa)
                        }
                    }

                   /* Log.i("info_tmdb", "Codigo: ${retorno.code()} ")
                    listaFilmes?.forEach { filme ->
                        val id = filme.id
                        val title = filme.title
                        Log.i("info_tmdb", "$id - $title ")
                    }*/
                }else{
                    Log.i(TAG, "Erro na requisição  -> codigo do erro: ${retorno.code()} ")
                }

            }

        }

    }

    override fun onStart() {
        super.onStart()

    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }

}