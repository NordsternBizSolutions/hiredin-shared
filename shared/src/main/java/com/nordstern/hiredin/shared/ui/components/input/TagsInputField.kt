package com.nordstern.hiredin.shared.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsInputField(tags: List<String>, onTagsChange: (List<String>) -> Unit, modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    Column(modifier) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) { tags.forEach { FilterChip(selected = true, onClick = { onTagsChange(tags - it) }, label = { Text(it) }) } }
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Add tag") }, modifier = Modifier.fillMaxWidth())
        if (input.isNotBlank()) TextButton(onClick = { onTagsChange(tags + input.trim()); input = "" }) { Text("Add") }
    }
}
