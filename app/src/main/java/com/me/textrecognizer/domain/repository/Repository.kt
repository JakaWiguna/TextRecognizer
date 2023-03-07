package com.me.textrecognizer.domain.repository

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.text.Text
import com.me.textrecognizer.data.remote.dto.DirectionsResponse
import com.me.textrecognizer.domain.model.DataFirebase
import com.me.textrecognizer.utils.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getDirections(
        origins: String,
        destinations: String,
        mode: String = "driving",
        apiKey: String,
    ): Flow<Resource<DirectionsResponse>>

    suspend fun addDataToFirebase(
        text: String,
        distance: String,
        duration: String,
    ): Flow<Resource<String>>

    suspend fun editDataToFirebase(
        documentId: String,
        text: String,
        distance: String,
        duration: String,
    ): Flow<Resource<String>>

    suspend fun doTextRecognizer(
        context: Context,
        photoUri: Uri,
    ): Flow<Resource<Text>>

    suspend fun getDataFromFirebase(
        documentId: String,
    ): Flow<Resource<DataFirebase>>
}