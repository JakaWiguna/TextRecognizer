package com.me.textrecognizer.presentation.screen.main

import android.net.Uri
import com.me.textrecognizer.BuildConfig
import com.me.textrecognizer.data.remote.dto.DirectionsResponse
import java.net.URLDecoder

data class MainState(
    val isLoading: Boolean = false,
    val apiKey: String = BuildConfig.MAPS_API_KEY,
    val mode: String = "driving",
    val destinations: String = URLDecoder.decode(
        "Plaza+Indonesia,+Jl.+M.H.+Thamrin+No.30,+Gondangdia,+Menteng,+Central+Jakarta+City,+Jakarta+10350",
        "UTF-8"
    ),
    val shouldShowCamera: Boolean = true,
    val shouldShowPhoto: Boolean = false,
    val photoUri: Uri? = null,
    val directionsResponse: DirectionsResponse? = null,
)