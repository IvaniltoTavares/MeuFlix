package com.example.meuflix

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meuflix.api.RetrofitService.Companion.filmeAPI
import com.example.meuflix.databinding.ActivityTrailerBinding
import com.example.meuflix.model.filmespopulares.Filme
import com.example.meuflix.model.videos.VideoResposta
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response

class TrailerActivity : YouTubeBaseActivity() {


    val API_KEY = "AIzaSyAfDX0TU11dGhpJK1ZJJcNMqSkfQbgE6mQ"


    private val binding by lazy {
        ActivityTrailerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        carregarTrailer()

        binding.btnTelaInicial.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }

    private fun carregarTrailer(){
        val KEY_ID = intent.getStringExtra("trailer")

        if (KEY_ID != null) {
            if (KEY_ID.isNotEmpty()){

                val youtubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
                youtubePlayerView.initialize(API_KEY, object: YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(provider: YouTubePlayer.Provider, player: YouTubePlayer, wasRestored: Boolean) {
                        if (!wasRestored) {
                            player.loadVideo(KEY_ID)
                        }
                    }

                    override fun onInitializationFailure(provider: YouTubePlayer.Provider, error: YouTubeInitializationResult) {
                        Toast.makeText(applicationContext, "Não existe trailer!!!", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }

    }


}