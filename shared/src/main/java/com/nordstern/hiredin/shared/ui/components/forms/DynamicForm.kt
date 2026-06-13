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

@Composable
fun DynamicForm(
    fields: List<FormField>,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    submitLabel: String = "Submit"
) {
    val errors = remember { mutableStateMapOf<String, String>() }
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        fields.forEach { field ->
            HiredInTextField(
                value = values[field.key] ?: "",
                onValueChange = { onValueChange(field.key, it); errors.remove(field.key) },
                label = field.label,
                error = errors[field.key]
            )
            Spacer(Modifier.height(12.dp))
        }
        Button(onClick = {
            var valid = true
            fields.forEach { field ->
                val value = values[field.key] ?: ""
                val error = when {
                    field.required && value.isBlank() -> "${field.label} is required"
                    else -> field.validator(value)
                }
                if (error != null) { errors[field.key] = error; valid = false }
            }
            if (valid) onSubmit()
        }, modifier = Modifier.fillMaxWidth()) { Text(submitLabel) }
    }
}
