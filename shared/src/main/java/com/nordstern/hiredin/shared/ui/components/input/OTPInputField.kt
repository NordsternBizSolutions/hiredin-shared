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
fun OTPInputField(digits: Int = 6, onComplete: (String) -> Unit, modifier: Modifier = Modifier) {
    var code by remember { mutableStateOf("") }
    OutlinedTextField(value = code, onValueChange = { if (it.length <= digits) { code = it; if (it.length == digits) onComplete(it) } }, modifier = modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
}
