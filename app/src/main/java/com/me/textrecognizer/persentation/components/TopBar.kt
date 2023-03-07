package com.me.textrecognizer.persentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.me.textrecognizer.persentation.navigation.Screen
import com.me.textrecognizer.ui.theme.TextRecognizerTheme

@Composable
fun TopBar(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val topBarDestination =
        TopBarDestination.values().any { it.route == currentDestination?.route }

    if (topBarDestination) {
        TextRecognizerTheme {
            when (currentDestination?.route) {
                TopBarDestination.MAIN.route -> {
                    DefaultTopBar(title = TopBarDestination.MAIN.label)
                }
                TopBarDestination.DETAIL.route -> {
                    DefaultTopBar(title = TopBarDestination.DETAIL.label,
                        navigationIcon = {
                            IconButton(onClick = {
                                navController.popBackStack()
                            }) {
                                Icon(Icons.Filled.ArrowBack, null, tint = Color.White)
                            }
                        })
                }


            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun DefaultTopBar(
    title: String, navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        elevation = 0.dp,
        title = {
            Text(
                text = title.uppercase(),
                style = TextStyle.Default.copy(
                    drawStyle = Stroke(
                        miter = 10f,
                        width = 5f,
                        join = StrokeJoin.Round
                    )
                ),
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

        },
        navigationIcon = navigationIcon,
        actions = actions
    )
}


enum class TopBarDestination(
    val route: String,
    val label: String,
) {
    MAIN(Screen.MainScreen.route, "Text Recognizer"),
    DETAIL(Screen.DetailScreen.route + "/{documentId}", "Text Recognizer"),
}
