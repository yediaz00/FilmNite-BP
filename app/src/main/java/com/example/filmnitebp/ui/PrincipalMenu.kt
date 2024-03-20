package com.example.filmnitebp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.filmnitebp.states.AppVars
import java.io.File

@Composable
fun MainMenu(navController: NavController) {

    @Composable
    fun BotonSala(url: String) {
        Button(
            onClick = {
                // Navegar a la pantalla deseada y pasar la URL como argumento
                AppVars.url = url
                navController.navigate("Sala")
            },
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            // Texto del botón
            Text(text = "Ir a otra pantalla")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
            ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                BotonSala(url = AppVars.streamItem.url)
            }
        }

    }
}