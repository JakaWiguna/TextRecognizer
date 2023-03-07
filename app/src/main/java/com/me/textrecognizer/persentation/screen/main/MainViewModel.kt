package com.me.textrecognizer.persentation.screen.main

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.text.Text
import com.me.textrecognizer.domain.use_case.DoTextRecognizerUseCase
import com.me.textrecognizer.domain.use_case.GetDirectionsUseCase
import com.me.textrecognizer.domain.use_case.AddDataToFirebaseUseCase
import com.me.textrecognizer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDirectionsUseCase: GetDirectionsUseCase,
    private val doTextRecognizerUseCase: DoTextRecognizerUseCase,
    private val addDataToFirebaseUseCase: AddDataToFirebaseUseCase,
) : ViewModel() {

    var state by mutableStateOf(MainState())

    private val _event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> get() = _event

    fun onEvent(event: MainEvent) {
        when (event) {
            is MainEvent.ShowToast -> {
                viewModelScope.launch {
                    _event.emit(MainEvent.ShowToast(event.message))
                }
            }
            is MainEvent.HandleImageCapture -> {
                state = state.copy(
                    shouldShowCamera = false,
                    photoUri = event.uri,
                    shouldShowPhoto = true
                )
            }
            is MainEvent.DoRetake -> {
                state = state.copy(
                    shouldShowCamera = true,
                    shouldShowPhoto = false
                )
            }
            is MainEvent.GoToDetailScreen -> Unit
        }
    }

    fun doProcess(context: Context) {
        try {
            val currentLocation = getCurrentLocation(context = context)
            currentLocation?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                doGetDirections(
                    context = context,
                    origins = latitude.toString() + URLDecoder.decode(
                        ",",
                        "UTF-8"
                    ) + longitude.toString()
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun doGetDirections(
        context: Context,
        origins: String,
    ) {
        viewModelScope.launch {
            getDirectionsUseCase
                .execute(
                    origins = origins,
                    destinations = state.destinations,
                    mode = state.mode,
                    apiKey = state.apiKey
                ).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            resource.isLoading.let { isLoading ->
                                state = state.copy(
                                    isLoading = isLoading
                                )
                            }
                        }
                        is Resource.Success -> {
                            resource.data?.let { data ->
                                state = state.copy(
                                    directionsResponse = data
                                )
                                if (data.status.equals("OK"))
                                    doRecognizer(context)
                            }
                        }
                        is Resource.Error -> {
                            resource.message?.let { message ->
                                _event.emit(MainEvent.ShowToast(message))
                            }
                        }
                    }
                }
        }
    }

    private suspend fun doRecognizer(context: Context) {
        state.photoUri?.let { photoUri ->
            doTextRecognizerUseCase.execute(
                context = context,
                photoUri = photoUri
            ).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        resource.isLoading.let { isLoading ->
                            state = state.copy(
                                isLoading = isLoading
                            )
                        }
                    }
                    is Resource.Success -> {
                        resource.data?.let { data ->
                            processResultText(resultText = data)
                        }

                    }
                    is Resource.Error -> {
                        resource.message?.let { message ->
                            _event.emit(MainEvent.ShowToast(message))
                        }
                    }
                }
            }
        }
    }

    private suspend fun doUpload(resultTextRecognizer: String) {
        addDataToFirebaseUseCase.execute(
            text = resultTextRecognizer,
            distance = state.directionsResponse?.rows?.get(0)?.elements?.get(0)?.distance?.text
                ?: "",
            duration = state.directionsResponse?.rows?.get(0)?.elements?.get(0)?.duration?.text
                ?: "",
        ).collect { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.isLoading.let { isLoading ->
                        state = state.copy(
                            isLoading = isLoading
                        )
                    }
                }
                is Resource.Success -> {
                    resource.data?.let { data ->
                        _event.emit(MainEvent.GoToDetailScreen(data))
                    }
                }
                is Resource.Error -> {
                    resource.message?.let { message ->
                        _event.emit(MainEvent.ShowToast(message))
                    }
                }
            }
        }
    }

    private fun getCurrentLocation(context: Context): Location? {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            val locationProvider = LocationManager.NETWORK_PROVIDER
            locationManager.getLastKnownLocation(locationProvider)
        } catch (e: SecurityException) {
            Log.e("debug", "getCurrentLocation: " + e.localizedMessage)
            null
        }
    }

    private suspend fun processResultText(resultText: Text) {
        if (resultText.textBlocks.size == 0) {
            return
        }
        val textBuilder = StringBuilder()
        resultText.textBlocks.forEach {
            textBuilder.append(it.text + "\n")
        }
        doUpload(resultTextRecognizer = textBuilder.toString())
    }
}