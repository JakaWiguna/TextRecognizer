package com.me.textrecognizer.presentation.screen.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.me.textrecognizer.presentation.components.LoadingDialog

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
) {
    val state = viewModel.state
    val context: Context = LocalContext.current

    LaunchedEffect(key1 = context) {
        viewModel.event.collect { event ->
            when (event) {
                is DetailEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.text,
                onValueChange = { viewModel.onEvent(DetailEvent.TextChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "text") },
                placeholder = { Text(text = "text") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.distance,
                onValueChange = { viewModel.onEvent(DetailEvent.DistanceChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "distance") },
                placeholder = { Text(text = "distance") },
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = state.duration,
                onValueChange = { viewModel.onEvent(DetailEvent.DurationChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "duration") },
                placeholder = { Text(text = "duration") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.onEvent(DetailEvent.DoSave) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }

        LoadingDialog(
            isLoading = state.isLoading, message = "Please wait...",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}