package com.nordstern.hiredin.shared.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, placeholder: String = "Search", modifier: Modifier = Modifier) {
    OutlinedTextField(value = query, onValueChange = onQueryChange, placeholder = { Text(placeholder) }, modifier = modifier, singleLine = true)
}
