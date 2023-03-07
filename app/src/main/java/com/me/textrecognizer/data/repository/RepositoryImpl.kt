package com.me.textrecognizer.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.me.textrecognizer.data.remote.api.NetworkService
import com.me.textrecognizer.data.remote.dto.DirectionsResponse
import com.me.textrecognizer.domain.model.DataFirebase
import com.me.textrecognizer.domain.repository.Repository
import com.me.textrecognizer.utils.DispatcherProvider
import com.me.textrecognizer.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: NetworkService,
    private val dispatcherProvider: DispatcherProvider,
    private val db: FirebaseFirestore,
) : Repository {

    companion object {
        const val COLLECTION: String = "recognizer"
    }

    override suspend fun getDirections(
        origins: String,
        destinations: String,
        mode: String,
        apiKey: String,
    ): Flow<Resource<DirectionsResponse>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = api.getDirections(
                    origins = origins,
                    destinations = destinations,
                    mode = mode,
                    apiKey = apiKey
                )
                response.let {
                    if (it.isSuccessful) {
                        val tokenResp = it.body()!!
                        emit(Resource.Success(tokenResp))
                    } else {
                        emit(Resource.Error(message = response.errorBody()!!.string()))
                    }
                }
            } catch (e: HttpException) {
                emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                emit(Resource.Error("Couldn't load data"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun addDataToFirebase(
        text: String,
        distance: String,
        duration: String,
    ): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val data = mapOf(
                    "text" to text,
                    "distance" to distance,
                    "duration" to duration
                )
                val documentReference = db.collection(COLLECTION).add(data).await()
                emit(Resource.Success(documentReference.id))
            } catch (e: Exception) {
                emit(Resource.Error("Error adding document"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun editDataToFirebase(
        documentId: String,
        text: String,
        distance: String,
        duration: String,
    ): Flow<Resource<String>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val data = mapOf(
                    "text" to text,
                    "distance" to distance,
                    "duration" to duration
                )
                db.collection(COLLECTION).document(documentId).update(data)
                emit(Resource.Success("success"))
            } catch (e: Exception) {
                emit(Resource.Error("Error editing document"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun getDataFromFirebase(documentId: String): Flow<Resource<DataFirebase>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val results = db.collection(COLLECTION).get().await()
                val document = results.filter { it.id == documentId }.mapNotNull {
                    DataFirebase(
                        text = it.data["text"] as String,
                        distance = it.data["distance"] as String,
                        duration = it.data["duration"] as String
                    )
                }
                emit(
                    Resource.Success(document.first())
                )
            } catch (e: Exception) {
                emit(Resource.Error("Error get data document"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }

    override suspend fun doTextRecognizer(context: Context, photoUri: Uri): Flow<Resource<Text>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                val image = InputImage.fromFilePath(context, photoUri)
                val firebaseVisionText = recognizer.process(image).await()
                emit(Resource.Success(firebaseVisionText))
            } catch (e: Exception) {
                emit(Resource.Error("Error processing Image"))
            }
            emit(Resource.Loading(false))
        }.flowOn(dispatcherProvider.io)
    }
}