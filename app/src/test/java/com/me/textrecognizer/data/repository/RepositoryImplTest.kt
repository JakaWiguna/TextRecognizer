package com.me.textrecognizer.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.me.textrecognizer.data.remote.api.NetworkService
import com.me.textrecognizer.data.remote.dto.*
import com.me.textrecognizer.utils.Resource
import com.me.textrecognizer.utils.TestDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
class RepositoryImplTest {

    @Mock
    lateinit var api: NetworkService

    @Mock
    lateinit var db: FirebaseFirestore

    private lateinit var repository: RepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = RepositoryImpl(
            api = api,
            dispatcherProvider = TestDispatcherProvider(),
            db = db
        )
    }

    @Test
    fun `getDirection data from remote`() {
        runBlocking {
            val expectedResult = DirectionsResponse(
                originAddresses = listOf(
                    "Senayan City Jakarta"
                ), destinationAddresses = listOf(
                    "Plaza Indonesia Jakarta"
                ), rows = listOf(
                    Row(
                        elements = listOf(
                            Element(
                                distance = Distance(
                                    text = "10 km", value = 1000
                                ), duration = Duration(
                                    text = "5 min", value = 500
                                ), status = "OK"
                            )
                        )
                    )
                ), status = "OK"
            )

            val responseAPI = DirectionsResponse(
                originAddresses = listOf(
                    "Senayan City Jakarta"
                ), destinationAddresses = listOf(
                    "Plaza Indonesia Jakarta"
                ), rows = listOf(
                    Row(
                        elements = listOf(
                            Element(
                                distance = Distance(
                                    text = "10 km", value = 1000
                                ), duration = Duration(
                                    text = "5 min", value = 500
                                ), status = "OK"
                            )
                        )
                    )
                ), status = "OK"
            )

            Mockito.`when`(
                api.getDirections(
                    origins = "FAKE_ORIGIN",
                    destinations = "FAKE_DESTINATION",
                    mode = "FAKE_MODE",
                    apiKey = "FAKE_API"
                )
            ).thenReturn(Response.success(responseAPI))

            repository.getDirections(
                origins = "FAKE_ORIGIN",
                destinations = "FAKE_DESTINATION",
                mode = "FAKE_MODE",
                apiKey = "FAKE_API"
            ).take(3).collect { resource ->
                // then
                when (resource) {
                    is Resource.Loading -> {
                        assert(resource.isLoading || !resource.isLoading)
                    }
                    is Resource.Success -> {
                        assert(resource.data == expectedResult)
                    }
                    else -> {
                        // should not reach here
                        assert(false)
                    }
                }
            }
        }
    }

    @Test
    fun `getDirection should return error when API call is unsuccessful`() = runBlocking {
        // given
        val errorResponse = "{\"message\": \"Bad Request\"}"

        Mockito.`when`(
            api.getDirections(
                origins = "FAKE_ORIGIN",
                destinations = "FAKE_DESTINATION",
                mode = "FAKE_MODE",
                apiKey = "FAKE_API"
            )
        ).thenReturn(Response.error(400, errorResponse.toResponseBody()))

        // when
        repository.getDirections(
            origins = "FAKE_ORIGIN",
            destinations = "FAKE_DESTINATION",
            mode = "FAKE_MODE",
            apiKey = "FAKE_API"
        ).take(3).collect { resource ->
            println("resource = ${resource.message}")
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == errorResponse)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getDirection should emit error message with HTTPException`() = runBlocking {
        // given
        val errorResponse = "{\"message\": \"Bad Request\"}"
        val responseBody = errorResponse.toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<String>(400, responseBody)
        val httpException = HttpException(response)

        Mockito.`when`(
            api.getDirections(
                origins = "FAKE_ORIGIN",
                destinations = "FAKE_DESTINATION",
                mode = "FAKE_MODE",
                apiKey = "FAKE_API"
            )
        ).thenAnswer {
            throw httpException
        }

        repository.getDirections(
            origins = "FAKE_ORIGIN",
            destinations = "FAKE_DESTINATION",
            mode = "FAKE_MODE",
            apiKey = "FAKE_API"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == httpException.message)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

    @Test
    fun `getDirection should emit error message with IOException`() = runBlocking {
        // given
        val expectedResult = "Couldn't reach server. Check your internet connection."
        val ioException = IOException()

        Mockito.`when`(
            api.getDirections(
                origins = "FAKE_ORIGIN",
                destinations = "FAKE_DESTINATION",
                mode = "FAKE_MODE",
                apiKey = "FAKE_API"
            )
        ).thenAnswer {
            throw ioException
        }

        repository.getDirections(
            origins = "FAKE_ORIGIN",
            destinations = "FAKE_DESTINATION",
            mode = "FAKE_MODE",
            apiKey = "FAKE_API"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }


    @Test
    fun `getDirection should emit error message with Exception`() = runBlocking {
        // given
        val expectedResult = "Couldn't load data"
        val exception = Exception()

        Mockito.`when`(
            api.getDirections(
                origins = "FAKE_ORIGIN",
                destinations = "FAKE_DESTINATION",
                mode = "FAKE_MODE",
                apiKey = "FAKE_API"
            )
        ).thenAnswer {
            throw exception
        }

        // when
        repository.getDirections(
            origins = "FAKE_ORIGIN",
            destinations = "FAKE_DESTINATION",
            mode = "FAKE_MODE",
            apiKey = "FAKE_API"
        ).take(3).collect { resource ->
            // then
            when (resource) {
                is Resource.Loading -> {
                    assert(resource.isLoading || !resource.isLoading)
                }
                is Resource.Error -> {
                    assert(resource.message == expectedResult)
                }
                else -> {
                    // should not reach here
                    assert(false)
                }
            }
        }
    }

}