package com.nordstern.hiredin.shared.ui.components.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PasswordStrengthMeter(password: String, modifier: Modifier = Modifier) {
    val strength = when {
        password.length < 6 -> 0.25f
        password.length < 10 -> 0.5f
        password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 1f
        else -> 0.75f
    }
    val label = when {
        strength < 0.5f -> "Weak"
        strength < 0.75f -> "Medium"
        else -> "Strong"
    }
    Column(modifier) {
        LinearProgressIndicator(progress = { strength }, modifier = Modifier.fillMaxWidth())
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
