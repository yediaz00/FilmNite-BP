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
import androidx.compose.runtime.mutableStateOf
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


@OptIn(UnstableApi::class)
@Composable
fun MainScreen(url:String){
    val context= LocalContext.current

    val exoPlayer= ExoPlayer.Builder(context).build()
    val mediaItem= MediaItem.fromUri(Uri.parse(url))

    exoPlayer.setMediaItem(mediaItem)
    val playerView= PlayerView(context)

    var controllerStatus=false

    Screen()
    playerView.player=exoPlayer

    // Quitamos el boton de previous y next

    // playerView.setShowRewindButton(false)
    // playerView.setShowFastForwardButton(false)
    // val inicio=exoPlayer.currentPosition //ver el moment actual
    // exoPlayer.seekTo(50000)   //Prueba para ver el seekto

    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    var offsetY by remember { mutableFloatStateOf(0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    DisposableEffect(
        AndroidView(
            factory = {
                playerView.apply {
                    player=exoPlayer
                    useController=true
                    resizeMode= AspectRatioFrameLayout.RESIZE_MODE_FILL
                    layoutParams= FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }, Modifier
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            offsetY = dragAmount //vemos cuanto se ha movido, si es positivo se ha movido hacia abajo, si es negatio hacia arriba
                        },
                        onDragStart = { offset ->
                            //offsetY=offset.y
                        }, onDragEnd = {

                            if (offsetY < 0) {
                                if (!controllerStatus) {
                                    playerView.useController = true
                                    playerView.showController()
                                    controllerStatus = true
                                } else {
                                    Log.d(
                                        "User Input",
                                        "Drag end arriba @ $offsetY"
                                    ) //esto es para ver el funcionamiento
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
                                    )//esto es para ver el funcionamiento
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
                        val posy = it.y
                        Log.d("User Input", "Double tap x $posx")
                        Log.d("User Input", "Double tap y $posy")

                        if( posx >= (screenWidth/2).value ){
                            exoPlayer.seekForward(                                      )
                        }else{
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
                    detectHorizontalDragGestures(onHorizontalDrag = { change, dragAmount ->
                        offsetX = dragAmount
                    },
                        onDragStart = {}, onDragEnd = {
                            Log.d("User Input", "Drag end lateral @ $offsetX")
                        }, onDragCancel = {})
                }


        ) ){


        exoPlayer.prepare()
        playerView.setShowPreviousButton(false)
        playerView.setShowNextButton(false)
        //Para quitar antes y despues
        playerView.useController=false //Con esto no aparecen los controles, de forma que cuando hagamos que se deslize hacia arriba, aparecen.
        playerView.controllerHideOnTouch=false//Evitar que se quiten los controles con el toque
        playerView.controllerShowTimeoutMs=0//Nunca se van los ocntroles si estan en pantalla, de tal forma que haya que deslizar hacia abajo para quitarlos
        exoPlayer.playWhenReady=true




        onDispose {
            exoPlayer.release()
        }
    }

}
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
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
@Composable
fun Screen() {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

}



