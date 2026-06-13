package com.nordstern.hiredin.shared.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ImagePicker(label: String = "Choose image", onClick: () -> Unit, modifier: Modifier = Modifier) =
    FilePicker(label, onClick, modifier)
