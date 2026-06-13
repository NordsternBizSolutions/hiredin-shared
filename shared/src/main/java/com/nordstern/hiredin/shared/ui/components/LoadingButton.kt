package com.nordstern.hiredin.shared.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingButton(text: String, isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = onClick, enabled = !isLoading, modifier = modifier.fillMaxWidth()) {
        if (isLoading) CircularProgressIndicator() else Text(text)
    }
}
