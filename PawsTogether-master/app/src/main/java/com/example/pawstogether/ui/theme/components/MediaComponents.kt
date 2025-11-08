@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.example.pawstogether.ui.theme.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun MediaPicker(onMediaSelected: (Uri, String) -> Unit) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            // Lee el nombre del archivo de forma segura
            var fileName = "archivo"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex) ?: fileName
                }
            }
            onMediaSelected(uri, fileName)
        }
    }

    Button(onClick = { launcher.launch("*/*") }) {
        Text("Seleccionar medio (imagen o video)")
    }
}

@Composable
fun MediaPreview(uri: Uri) {
    val context = LocalContext.current
    val type = context.contentResolver.getType(uri)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        when {
            type?.startsWith("image/") == true -> {
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Previsualización de imagen",
                    modifier = Modifier.fillMaxSize()
                )
            }

            type?.startsWith("video/") == true -> {
                VideoPlayer(uri.toString())
            }

            else -> {
                Text(
                    "Tipo de archivo no soportado",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun VideoPlayer(uri: String) {
    val context = LocalContext.current

    // Crea y recuerda el ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
        }
    }

    // Actualizar el media item cuando cambie la uri y gestiona el ciclo de vida
    DisposableEffect(uri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
        onDispose {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                // En Media3 estas propiedades existen, pero están marcadas como @UnstableApi
                useController = true
                controllerAutoShow = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        update = { playerView ->
            playerView.player = exoPlayer
        }
    )
}
