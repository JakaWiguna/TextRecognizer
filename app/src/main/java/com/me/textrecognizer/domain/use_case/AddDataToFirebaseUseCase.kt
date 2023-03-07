package com.me.textrecognizer.domain.use_case

import com.me.textrecognizer.data.repository.RepositoryImpl
import com.me.textrecognizer.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddDataToFirebaseUseCase @Inject constructor(
    private val repository: RepositoryImpl,
) {
    suspend fun execute(
        text:String,
        distance:String,
        duration:String
    ): Flow<Resource<String>> {
        return repository.addDataToFirebase(
            text = text,
            distance = distance,
            duration = duration
        )
    }
}