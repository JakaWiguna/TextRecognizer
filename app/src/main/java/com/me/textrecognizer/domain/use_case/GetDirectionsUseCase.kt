package com.me.textrecognizer.domain.use_case

import com.me.textrecognizer.data.remote.dto.DirectionsResponse
import com.me.textrecognizer.data.repository.RepositoryImpl
import com.me.textrecognizer.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDirectionsUseCase @Inject constructor(
    private val repository: RepositoryImpl,
) {
    suspend fun execute(
        origins: String,
        destinations: String,
        mode: String,
        apiKey: String,
    ): Flow<Resource<DirectionsResponse>> {
        return repository.getDirections(
            origins = origins,
            destinations = destinations,
            mode = mode,
            apiKey = apiKey
        )
    }
}