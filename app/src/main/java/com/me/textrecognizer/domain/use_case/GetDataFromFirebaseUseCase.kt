package com.me.textrecognizer.domain.use_case

import com.me.textrecognizer.data.repository.RepositoryImpl
import com.me.textrecognizer.domain.model.DataFirebase
import com.me.textrecognizer.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDataFromFirebaseUseCase @Inject constructor(
    private val repository: RepositoryImpl,
) {
    suspend fun execute(
        documentId: String,
    ): Flow<Resource<DataFirebase>> {
        return repository.getDataFromFirebase(
            documentId = documentId
        )
    }
}