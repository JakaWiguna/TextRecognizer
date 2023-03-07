package com.me.textrecognizer.persentation.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.me.textrecognizer.persentation.screen.detail.DetailScreen
import com.me.textrecognizer.persentation.screen.main.MainScreen
import java.io.File
import java.util.concurrent.ExecutorService

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RootNavigationGraph(
    navController: NavHostController,
    outputDirectory: File,
    cameraExecutor: ExecutorService,
) {
    AnimatedNavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Screen.MainScreen.route,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = { fadeOut(animationSpec = tween(300)) },
    ) {
        composable(
            route = Screen.MainScreen.route,
        ) {
            MainScreen(
                viewModel = hiltViewModel(),
                outputDirectory = outputDirectory,
                cameraExecutor = cameraExecutor,
                onSuccessUpload = { documentId ->
                    navController.navigate(Screen.DetailScreen.route + "/$documentId")
                }
            )
        }
        composable(
            route = Screen.DetailScreen.route + "/{documentId}",
            arguments = listOf(navArgument("documentId") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            documentId?.let {
                DetailScreen(viewModel = hiltViewModel())
            }
        }
    }
}

object Graph {
    const val ROOT = "root_graph"
}

open class Screen(val route: String) {
    object MainScreen : Screen(route = "MAIN")
    object DetailScreen : Screen(route = "DETAIL")
}