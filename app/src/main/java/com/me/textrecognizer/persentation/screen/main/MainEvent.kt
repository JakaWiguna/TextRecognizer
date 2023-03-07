package com.me.textrecognizer.persentation.screen.main

import android.net.Uri

sealed class MainEvent {
    data class ShowToast(val message: String) : MainEvent()
    data class HandleImageCapture(val uri:Uri) : MainEvent()
    data class GoToDetailScreen(val documentId: String) : MainEvent()
    object DoRetake : MainEvent()
}