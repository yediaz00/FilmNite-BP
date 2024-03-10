package com.example.filmnitebp




import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle

import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.filmnitebp.ui.theme.FilmNiteBPTheme



val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
val url2 = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
val url3="http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"

class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Estas 2 funciones sirven para que la status bar no aparezca de forma que se vea perfecto el video.
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContent {
            FilmNiteBPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(url)
                }
            }
        }
    }
}


//Esto es  lo relacionado con el videoplayer, la idea seria moverlo a su propia clase, por ahora solo eran pruebas de funcionamiento

@OptIn(UnstableApi::class)
@Composable
fun MainScreen(url:String){
    val context= LocalContext.current

    val exoPlayer= ExoPlayer.Builder(context).build()
    val mediaItem= MediaItem.fromUri(Uri.parse(url))
    exoPlayer.setMediaItem(mediaItem)



    val playerView=PlayerView(context)
    var controllerStatus=false


    Screen()
    playerView.player=exoPlayer

    //Quitamos el boton de previous y next


   // playerView.setShowRewindButton(false)
   // playerView.setShowFastForwardButton(false)
   // val inicio=exoPlayer.currentPosition //ver el moment actual
    // exoPlayer.seekTo(50000)   //Prueba para ver el seekto

    var offsetY by remember { mutableStateOf(0f) }
    DisposableEffect(AndroidView(factory = {playerView.apply {
        player=exoPlayer
        useController=true
        resizeMode=AspectRatioFrameLayout.RESIZE_MODE_FILL
        layoutParams=FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
        },Modifier.pointerInput(Unit){

        detectVerticalDragGestures(onVerticalDrag = {change, dragAmount ->
            change.consume()
            offsetY=dragAmount//vemos cuanto se ha movido, si es positivo se ha movido hacia abajo, si es negatio hacia arriba

        },
            onDragStart = {offset ->
                //offsetY=offset.y
            },onDragEnd = {

                if(offsetY<0){
                    if(!controllerStatus) {
                        playerView.useController = true
                        playerView.showController()
                        controllerStatus = true
                    }else{
                        Log.d("User Input", "Drag end arriba @ ${offsetY}") //esto es para ver el funcionamiento
                    }
                }else{
                    if(controllerStatus){
                        playerView.hideController()
                        playerView.useController=false
                        controllerStatus=false
                    }else{
                        Log.d("User Input", "Drag end abajo @ ${offsetY}")//esto es para ver el funcionamiento
                    }

                }

                                         }, onDragCancel = {})

        detectTapGestures(onTap = {}, onDoubleTap = {},
            onLongPress = {}, onPress = {})//este no me funciona, a ver si alguien puede hacer algo


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

