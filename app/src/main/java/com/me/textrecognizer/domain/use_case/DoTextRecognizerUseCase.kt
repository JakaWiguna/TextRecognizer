package com.me.textrecognizer.domain.use_case

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.text.Text
import com.me.textrecognizer.data.repository.RepositoryImpl
import com.me.textrecognizer.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DoTextRecognizerUseCase @Inject constructor(
    private val repository: RepositoryImpl,
) {
    suspend fun execute(
        context: Context,
        photoUri: Uri,
    ): Flow<Resource<Text>> {
        return repository.doTextRecognizer(
            context = context,
            photoUri = photoUri,
        )
    }
}