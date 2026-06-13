package com.nordstern.hiredin.shared.ui.components.input

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RichTextEditor(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "Content") {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = modifier.fillMaxWidth().heightIn(min = 120.dp), minLines = 4)
}
