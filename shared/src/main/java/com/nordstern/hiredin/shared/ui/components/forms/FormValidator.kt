package com.nordstern.hiredin.shared.ui.components.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nordstern.hiredin.shared.ui.components.common.HiredInTextField

object FormValidator {
    fun required(value: String, fieldName: String = "Field"): String? =
        if (value.isBlank()) "$fieldName is required" else null

    fun email(value: String): String? =
        if (!value.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) "Invalid email address" else null

    fun minLength(value: String, min: Int): String? =
        if (value.length < min) "Must be at least $min characters" else null
}
