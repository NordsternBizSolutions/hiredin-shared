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
fun CurrencyInputField(value: String, onValueChange: (String) -> Unit, currency: String = "AED", modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = { if (it.matches(Regex("^[0-9.]*$"))) onValueChange(it) }, label = { Text("Amount ()") }, modifier = modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
}
