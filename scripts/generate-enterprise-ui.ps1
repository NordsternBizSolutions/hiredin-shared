# Generates remaining enterprise UI component files
$base = "c:\Users\My Computer\AndroidStudioProjects\hiredinshared\shared\src\main\java\com\nordstern\hiredin\shared\ui"
$pkg = "com.nordstern.hiredin.shared.ui"

function Write-File($path, $content) {
    $full = Join-Path $base $path
    $dir = Split-Path $full -Parent
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $full -Value $content -Encoding UTF8
}

# Navigation
Write-File "components/navigation/NavigationComponents.kt" @"
package $pkg.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class NavItem(val label: String, val route: String, val badge: Int? = null)

@Composable
fun BottomNavigationBar(items: List<NavItem>, selectedRoute: String, onItemSelected: (NavItem) -> Unit, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.route == selectedRoute,
                onClick = { onItemSelected(item) },
                icon = { Icon(Icons.Default.Menu, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(title: String, modifier: Modifier = Modifier, onNavigationClick: (() -> Unit)? = null, actions: @Composable () -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        navigationIcon = { if (onNavigationClick != null) IconButton(onClick = onNavigationClick) { Icon(Icons.Default.Menu, "Menu") } },
        actions = { actions() },
        modifier = modifier
    )
}

@Composable
fun NavigationDrawer(isOpen: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    ModalNavigationDrawer(drawerState = rememberDrawerState(if (isOpen) DrawerValue.Open else DrawerValue.Closed), drawerContent = { ModalDrawerSheet { content() } }, content = {})
}

@Composable
fun TabLayout(tabs: List<String>, selectedIndex: Int, onTabSelected: (Int) -> Unit, modifier: Modifier = Modifier) {
    TabRow(selectedTabIndex = selectedIndex, modifier = modifier) {
        tabs.forEachIndexed { index, title ->
            Tab(selected = index == selectedIndex, onClick = { onTabSelected(index) }, text = { Text(title) })
        }
    }
}

@Composable
fun BreadcrumbNav(items: List<String>, modifier: Modifier = Modifier) {
    Text(items.joinToString(" > "), modifier = modifier, style = MaterialTheme.typography.labelMedium)
}
"@

# Input fields
Write-File "components/input/InputComponents.kt" @"
package $pkg.components.input

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

@Composable
fun PhoneInputField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "Phone") {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
}

@Composable
fun CurrencyInputField(value: String, onValueChange: (String) -> Unit, currency: String = "AED", modifier: Modifier = Modifier) {
    OutlinedTextField(value = value, onValueChange = { if (it.matches(Regex("^[0-9.]*$"))) onValueChange(it) }, label = { Text("Amount ($currency)") }, modifier = modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
}

@Composable
fun PasswordStrengthMeter(password: String, modifier: Modifier = Modifier) {
    val strength = when { password.length < 6 -> 0.25f; password.length < 10 -> 0.5f; password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 1f; else -> 0.75f }
    Column(modifier) { LinearProgressIndicator(progress = { strength }, modifier = Modifier.fillMaxWidth()); Text(when { strength < 0.5f -> "Weak" else if (strength < 0.75f) "Medium" else "Strong" }, style = MaterialTheme.typography.labelSmall) }
}

@Composable
fun AutoCompleteField(value: String, onValueChange: (String) -> Unit, suggestions: List<String>, modifier: Modifier = Modifier, label: String = "Search") {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(value = value, onValueChange = { onValueChange(it); expanded = true }, label = { Text(label) }, modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable).fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            suggestions.filter { it.contains(value, ignoreCase = true) }.forEach { s ->
                DropdownMenuItem(text = { Text(s) }, onClick = { onValueChange(s); expanded = false })
            }
        }
    }
}

@Composable
fun TagsInputField(tags: List<String>, onTagsChange: (List<String>) -> Unit, modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf("") }
    Column(modifier) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) { tags.forEach { FilterChip(selected = true, onClick = { onTagsChange(tags - it) }, label = { Text(it) }) } }
        OutlinedTextField(value = input, onValueChange = { input = it }, label = { Text("Add tag") }, modifier = Modifier.fillMaxWidth())
        if (input.isNotBlank()) TextButton(onClick = { onTagsChange(tags + input.trim()); input = "" }) { Text("Add") }
    }
}

@Composable
fun RichTextEditor(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, label: String = "Content") {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = modifier.fillMaxWidth().heightIn(min = 120.dp), minLines = 4)
}
"@

Write-Host "Enterprise UI components generated"
