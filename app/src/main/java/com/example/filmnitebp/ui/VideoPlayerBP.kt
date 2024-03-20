package com.example.filmnitebp.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.filmnitebp.states.AppVars


/*
* La siguiente función composible es usada para poder mostrar un reproductor de
* video el cual utiliza una url para poder streamear el video.
* */
    @OptIn(UnstableApi::class)
    @Composable
    fun MainScreen(navController: NavController){
    // Obtener la URL de la pantalla anterior
    val backStackEntry: NavBackStackEntry? = navController.previousBackStackEntry
    val url = "http://piola.cloudns.nz:12012"+AppVars.url
    Screen()

    val context= LocalContext.current
    val exoPlayer= ExoPlayer.Builder(context).build()
    val mediaItem= MediaItem.fromUri(Uri.parse(url))//Aqui pasamos la uri del video
    val playerView= PlayerView(context)
    var controllerStatus=false//Esta variable se utiliza para el menu de controles

    /*
    * Tomamos las dimensiones de la pantalla para que el doble tap sea dinamico
    * */
    val displayMetrics = context.resources.displayMetrics
    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels


    var offsetY by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    exoPlayer.setMediaItem(mediaItem)
    playerView.player=exoPlayer

    AndroidView(factory = {playerView.apply {
        player=exoPlayer
        useController=true
        resizeMode= AspectRatioFrameLayout.RESIZE_MODE_FILL
        layoutParams= FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
    }, Modifier
            /*
            * pointerInput se utiliza para poder ver los distintos eventos que ocurren por pantalla
            * de forma que tendremos que tener uno por cada evento que queramos ver.
            * Por un lado tendremos verticalDrag para poder sacar y quitar los controles del video
            * Luego tenemos de forma horizontal, que ahora no hace nada pero luego se podrán usar.
            * Por último tenemos tapGesture que usamos el doble tap para avanzar y retroceder video facilmente.
            * */
            .pointerInput(Unit) {
                detectVerticalDragGestures(onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    offsetY = dragAmount //vemos cuanto se ha movido, si es positivo se ha movido hacia abajo, si es negatio hacia arriba

                },
                    onDragStart = {},
                    onDragEnd = {
                        /*
                        * En caso de que arrastremos hacia arriba y los controles no estén desplegados,
                        * los desplegaremos. En caso de que se deslica hacia abajo y los controles esten mostrados,
                        * entonces se ocltarán, los otros casos se usarán más adelante, por ahora tienen un log para
                        * poder ver los valores que se toman y como funciona.
                        * */
                        if (offsetY < 0) {
                            if (!controllerStatus) {
                                playerView.useController = true
                                playerView.showController()
                                controllerStatus = true
                            } else {
                                Log.d("User Input","Drag end arriba @ $offsetY")
                            }
                        } else {
                            if (controllerStatus) {
                                playerView.hideController()
                                playerView.useController = false
                                controllerStatus = false
                            } else {
                                Log.d(
                                    "User Input",
                                    "Drag end abajo @ $offsetY"
                                )
                            }

                        }

                    }, onDragCancel = {})

            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    val posxtap = it.x
                    Log.d("User Input", " tap x $posxtap")

                }, onDoubleTap = {
                    val posx = it.x
                    Log.d("User Input", "Double tap x $posx")
                    if (posx >= (width / 2)) {
                        exoPlayer.seekForward()
                    } else {
                        exoPlayer.seekBack()
                    }

                },
                    onLongPress = {
                        Log.d("User Input", "Long press @ ")
                    }, onPress = {
                        Log.d("User Input", "Press")
                    })
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(onHorizontalDrag = { _, dragAmount ->
                    offsetX = dragAmount
                },
                    onDragStart = {}, onDragEnd = {
                        Log.d("User Input", "Drag end lateral @ $offsetX")
                    }, onDragCancel = {})
            }
        )

        DisposableEffect(key1 = Unit){
            exoPlayer.prepare()
            playerView.setShowPreviousButton(false)//Quitamos el boton de anterior
            playerView.setShowNextButton(false)//Quitamos el boton de siguiente
            playerView.useController=false //Con esto no aparecen los controles, de forma que cuando hagamos que se deslize hacia arriba, aparecen.
            playerView.controllerHideOnTouch=false//Evitar que se quiten los controles con el toque
            playerView.controllerShowTimeoutMs=0//Nunca se van los ocntroles si estan en pantalla, de tal forma que haya que deslizar hacia abajo para quitarlos
            onDispose {
                exoPlayer.release()
            }
        }

    }


    /*
    * Función utilizada para poder recuperar el la activity del contexto.
    * */
    fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }


    /*
    * La siguiente función sirve para poder mantener la orientación de la pantalla de una
    * forma concreta.
    * */
    @Composable
    fun LockScreenOrientation(orientation: Int) {
        val context = LocalContext.current

        DisposableEffect(Unit) {
            val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
            val originalOrientation = activity.requestedOrientation
            activity.requestedOrientation = orientation
            onDispose {
                // restore original orientation when view disappears
                activity.requestedOrientation = originalOrientation
            }
        }
    }

    /*
    * Funcion que hace que la vista se fuerce a estar en apaisado, de forma que se pueda
    * ver mejor el video sin romper el aspect ratio.
    * */
    @Composable
    fun Screen() {
        LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    }



