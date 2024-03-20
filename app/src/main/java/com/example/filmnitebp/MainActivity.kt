package com.example.filmnitebp

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.filmnitebp.model.Sala
import com.example.filmnitebp.states.AppVars
import com.example.filmnitebp.ui.MainMenu
import com.example.filmnitebp.ui.MainScreen
import com.example.filmnitebp.ui.theme.FilmNiteBPTheme
import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL


const val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
const val urlJ="http://piola.cloudns.nz:12012/sala/stream"


@Suppress("DEPRECATION")
class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Estas 2 funciones sirven para que la status bar no aparezca de forma que se vea perfecto el video.
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val urlLista = Url("http://piola.cloudns.nz:12012/sala/lista");

        val cliente = HttpClient()




        GlobalScope.launch {
            val respuesta = cliente.get(urlLista)
            val entrada = respuesta.body<String>()
			
            val gson = Gson()
			//todo Construir una instancia completa de Sala
            val salita = gson?.fromJson(entrada, Sala.Stream::class.java)
			AppVars.streamItem = salita!!

            setContent {
                FilmNiteBPTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navControl = rememberNavController()

                        NavHost(navController = navControl, startDestination = "Principal") {
                            composable("Principal") {
                                MainMenu(navController = navControl)
                            }

                            composable("Sala") {
                                MainScreen(navController = navControl)
                            }
                        }
                    }
                }
            }
        }
    }
}




