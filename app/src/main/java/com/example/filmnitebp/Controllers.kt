package com.example.filmnitebp

import androidx.media3.common.Player
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

data class Jump(val old : Player.PositionInfo, val new : Player.PositionInfo)

class RemoteEcho (
    private val client : HttpClient,
    private val serverAPIUrl : Url,
    private val ctx : CoroutineContext
) : Player.Listener {

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying);
        ctx. {
            client.post(serverAPIUrl) {
                url {
                    appendPathSegments(if (isPlaying) "play" else "pause"))
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
        client.post(serverAPIUrl) {
            url {
                appendPathSegments("jump")
            }
            contentType(ContentType.Application.Json)
            setBody(Jump(oldPosition, newPosition))
        }
    }
}