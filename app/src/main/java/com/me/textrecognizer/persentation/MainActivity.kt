package com.me.textrecognizer.persentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.me.textrecognizer.R
import com.me.textrecognizer.persentation.components.AppPermission
import com.me.textrecognizer.persentation.components.TopBar
import com.me.textrecognizer.persentation.navigation.RootNavigationGraph
import com.me.textrecognizer.ui.theme.TextRecognizerTheme
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberAnimatedNavController()
            TextRecognizerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        topBar = { TopBar(navController = navController) }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            AppPermission {
                                RootNavigationGraph(
                                    navController = navController,
                                    outputDirectory = outputDirectory,
                                    cameraExecutor = cameraExecutor,
                                )
                            }
                        }
                    }
                }
            }
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}