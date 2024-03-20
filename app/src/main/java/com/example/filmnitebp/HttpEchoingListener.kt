package com.example.filmnitebp

import androidx.media3.common.Player
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class Jump(val old : Player.PositionInfo, val new : Player.PositionInfo)

class HttpEchoingListener (
    private val client : HttpClient,
    private val serverAPIUrl : Url,
    private val scp : CoroutineScope
) : Player.Listener {

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        scp.launch() {
            client.post(serverAPIUrl) {
                url {
                    appendPathSegments(if (isPlaying) "playPause" else "playPause")
                }
            }
        }
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        scp.launch() {
            client.post(serverAPIUrl) {
                url {
                    appendPathSegments(
                        "moveto",
                        (newPosition.positionMs/1000.0).toString())
                }
            }
        }
    }
}