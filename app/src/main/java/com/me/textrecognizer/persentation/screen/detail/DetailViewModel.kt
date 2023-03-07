package com.me.textrecognizer.persentation.screen.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.me.textrecognizer.domain.use_case.EditDataToFirebaseUseCase
import com.me.textrecognizer.domain.use_case.GetDataFromFirebaseUseCase
import com.me.textrecognizer.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getDataFromFirebaseUseCase: GetDataFromFirebaseUseCase,
    private val editDataToFirebaseUseCase: EditDataToFirebaseUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val documentId: String = checkNotNull(savedStateHandle["documentId"])

    var state by mutableStateOf(DetailState())

    private val _event = MutableSharedFlow<DetailEvent>()
    val event: SharedFlow<DetailEvent> get() = _event

    fun onEvent(event: DetailEvent) {
        when (event) {
            is DetailEvent.ShowToast -> {
                viewModelScope.launch {
                    _event.emit(DetailEvent.ShowToast(event.message))
                }
            }
            is DetailEvent.DistanceChanged -> {
                state = state.copy(
                    distance = event.distance
                )
            }
            is DetailEvent.DurationChanged -> {
                state = state.copy(
                    duration = event.duration
                )
            }
            is DetailEvent.TextChanged -> {
                state = state.copy(
                    text = event.text
                )
            }
            is DetailEvent.DoSave -> {
                doEditDataToFirebase(documentId = documentId)
            }
        }
    }

    init {
        doGetDataFromFirebase(documentId = documentId)
    }

    private fun doGetDataFromFirebase(
        documentId: String,
    ) {
        viewModelScope.launch {
            getDataFromFirebaseUseCase
                .execute(
                    documentId = documentId
                )
                .collect { resource ->
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
                                Log.e("TAG", "doGetDataFromFirebase: $data")
                                state = state.copy(
                                    text = data.text,
                                    distance = data.distance,
                                    duration = data.duration
                                )
                            }
                        }
                        is Resource.Error -> {
                            resource.message?.let { message ->
                                _event.emit(DetailEvent.ShowToast(message))
                            }
                        }
                    }
                }
        }
    }

    private fun doEditDataToFirebase(
        documentId: String,
    ) {
        viewModelScope.launch {
            editDataToFirebaseUseCase
                .execute(
                    documentId = documentId,
                    text = state.text,
                    distance = state.distance,
                    duration = state.duration
                )
                .collect { resource ->
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
                                _event.emit(DetailEvent.ShowToast(data))
                            }
                        }
                        is Resource.Error -> {
                            resource.message?.let { message ->
                                _event.emit(DetailEvent.ShowToast(message))
                            }
                        }
                    }
                }
        }
    }
}