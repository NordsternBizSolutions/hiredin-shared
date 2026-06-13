package com.nordstern.hiredin.shared.ui.components.forms

data class FormField(
    val key: String,
    val label: String,
    val required: Boolean = false,
    val validator: (String) -> String? = { null }
)
