package com.example.filmnitebp

import android.app.Application
import android.net.Uri
import android.provider.MediaStore


data class MetaData(
    val fileName:String


)

interface MetaDataReader {
    fun getMetaDataFromUri(contentUri: Uri):MetaData?
}



class MetaDataReaderImp(
    private val app:Application
):MetaDataReader{

    override fun getMetaDataFromUri(contentUri: Uri): MetaData? {
        if(contentUri.scheme!="content"){return null}
        val fileName=app.contentResolver
            .query(
                contentUri,
                arrayOf(MediaStore.Video.VideoColumns.DISPLAY_NAME),
                null,null, null
            )
            //el ? es un cursor
            ?.use {
                cursor->
                val index=cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                //16:52 video
            }
    }
}