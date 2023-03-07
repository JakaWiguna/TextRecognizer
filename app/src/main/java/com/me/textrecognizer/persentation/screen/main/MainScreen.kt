package com.me.textrecognizer.persentation.screen.main

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.me.textrecognizer.persentation.components.LoadingDialog
import java.io.File
import java.util.concurrent.ExecutorService

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    outputDirectory: File,
    cameraExecutor: ExecutorService,
    onSuccessUpload: (documentId: String) -> Unit,
) {
    val context: Context = LocalContext.current
    val state = viewModel.state

    LaunchedEffect(key1 = context) {
        viewModel.event.collect { event ->
            when (event) {
                is MainEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is MainEvent.GoToDetailScreen -> {
                    onSuccessUpload(event.documentId)
                }
                else -> {}
            }
        }
    }


    if (state.shouldShowCamera) {
        CameraScreen(
            outputDirectory = outputDirectory,
            executor = cameraExecutor,
            onImageCaptured = { viewModel.onEvent(MainEvent.HandleImageCapture(it)) },
            onError = { Log.e("debug", "View error:", it) }
        )
    }

    if (state.shouldShowPhoto) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = ImageRequest.Builder(
                        LocalContext.current
                    )
                        .data(state.photoUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Image",
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Button(onClick = {
                        viewModel.onEvent(MainEvent.DoRetake)
                    }, modifier = Modifier.weight(1f), enabled = !state.isLoading) {
                        Text(text = "Retake")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = {
                        viewModel.doProcess(context)
                    }, modifier = Modifier.weight(1f), enabled = !state.isLoading) {
                        Text(text = "Process")
                    }
                }
            }

            LoadingDialog(
                isLoading = state.isLoading, message = "Please wait...",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}