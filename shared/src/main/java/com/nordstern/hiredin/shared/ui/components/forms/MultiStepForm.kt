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
fun MultiStepForm(
    steps: List<String>,
    currentStep: Int,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
        LinearProgressIndicator(progress = { (currentStep + 1f) / steps.size }, modifier = Modifier.fillMaxWidth())
        Text("Step ${currentStep + 1} of ${steps.size}: ${steps[currentStep]}", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 16.dp))
        content()
        Spacer(Modifier.height(16.dp))
        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth()) {
            if (currentStep > 0) Button(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Back") }
            Spacer(Modifier.height(0.dp).weight(0.1f))
            Button(onClick = onNext, modifier = Modifier.weight(1f)) {
                Text(if (currentStep < steps.lastIndex) "Next" else "Finish")
            }
        }
    }
}
