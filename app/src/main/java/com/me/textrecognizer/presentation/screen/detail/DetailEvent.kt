package com.me.textrecognizer.presentation.screen.detail

sealed class DetailEvent {
    data class ShowToast(val message: String) : DetailEvent()
    data class TextChanged(val text: String) : DetailEvent()
    data class DistanceChanged(val distance: String) : DetailEvent()
    data class DurationChanged(val duration: String) : DetailEvent()
    object DoSave : DetailEvent()
}