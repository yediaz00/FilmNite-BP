package com.example.videoviewcompose

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle

):ViewModel() {
    private val videoUris=savedStateHandle.getStateFlow("videoUris", emptyList<Uri>())
    val videoItems=videoUris.map {
        uris-> uris.map {uri ->
            VideoItem(contentUri = uri,
                        mediaItem = MediaItem.fromUri(uri),
                        name="No name")

    }
    }

}