package com.example.meuflix.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.meuflix.adapter.FilmeAdapter.FilmeViewHolder
import com.example.meuflix.api.RetrofitService
import com.example.meuflix.databinding.ItemFilmeBinding
import com.example.meuflix.model.RespostaPesquisa.FilmePesquisa
import com.example.meuflix.model.filmespopulares.Filme
import com.squareup.picasso.Picasso

class PesquisaAdapter(
    val onClick:(Filme) -> Unit
):RecyclerView.Adapter<PesquisaAdapter.PesquisaViewHolder>() {

    var listaFilmes = mutableListOf<Filme>()

    fun adicionarLista(lista:MutableList<Filme>) {
        this.listaFilmes.addAll(lista)
        notifyDataSetChanged()
    }
    inner class PesquisaViewHolder(itemFilme:ItemFilmeBinding):RecyclerView.ViewHolder(itemFilme.root){

        private val binding: ItemFilmeBinding

            init {
                this.binding = itemFilme
            }

        fun bind (filme:Filme){
            val nomeImagem = filme.backdrop_path
            val tamanhoImagem = "w780"
            val url_base = RetrofitService.BASE_URL_IMAGE

            val url = url_base + tamanhoImagem + nomeImagem

            Picasso.get()
                .load(url)
                .into(binding.imgItemFilme)

            binding.textTitulo.text = filme.title

            binding.clIten.setOnClickListener {
                onClick(filme)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): PesquisaViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemFilmeView = ItemFilmeBinding.inflate(
            layoutInflater,parent, false
        )
        return PesquisaViewHolder(itemFilmeView)
    }

    override fun onBindViewHolder(holder: PesquisaViewHolder, position: Int) {
        val filme = listaFilmes[position]
        holder.bind(filme)
    }

    override fun getItemCount(): Int {
        return listaFilmes.size
    }

}