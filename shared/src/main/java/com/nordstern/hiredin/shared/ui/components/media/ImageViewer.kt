package com.nordstern.hiredin.shared.ui.components.media

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageViewer(url: String, modifier: Modifier = Modifier) {
    AsyncImage(model = url, contentDescription = null, modifier = modifier.fillMaxSize(), contentScale = ContentScale.Fit)
}
