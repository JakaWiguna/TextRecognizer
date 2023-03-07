package com.me.textrecognizer.presentation.screen.detail

data class DetailState(
    val isLoading: Boolean = false,
    val text: String = "",
    val distance: String = "",
    val duration: String = "",
)

