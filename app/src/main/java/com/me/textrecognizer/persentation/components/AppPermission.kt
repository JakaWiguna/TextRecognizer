package com.me.textrecognizer.persentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppPermission(
    content: @Composable () -> Unit,
) {
    val permissionsState = rememberMultiplePermissionsState(
        mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    if (permissionsState.allPermissionsGranted) {
        content()
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    text = getTextToShowGivenPermissions(
                        permissionsState.revokedPermissions,
                        permissionsState.shouldShowRationale
                    )
                )
                Spacer(modifier = Modifier.height(96.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = {
                        permissionsState.launchMultiplePermissionRequest()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Request permissions",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(600),
                        lineHeight = 24.sp
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        permissionsState.launchMultiplePermissionRequest()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissions: List<PermissionState>,
    shouldShowRationale: Boolean,
): String {
    val revokedPermissionsSize = permissions.size
    if (revokedPermissionsSize == 0) return ""

    val textToShow = StringBuilder().apply {
        append("The ")
    }

    for (i in permissions.indices) {
        textToShow.append(permissions[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }
            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    textToShow.append(
        if (shouldShowRationale) {
            " important. Please grant all of them for the app to function properly."
        } else {
            " denied. The app cannot function without them."
        }
    )
    return textToShow.toString()
}