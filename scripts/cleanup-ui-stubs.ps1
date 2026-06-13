# Removes duplicate UI stub files superseded by consolidated implementations
$root = "c:\Users\My Computer\AndroidStudioProjects\hiredinshared\shared\src\main\java\com\nordstern\hiredin\shared\ui"
$patterns = @(
  "components\common\HiredIn*.kt",
  "components\dialogs\*.kt",
  "components\feedback\*.kt",
  "components\cards\*.kt",
  "components\lists\*.kt",
  "components\forms\Form*.kt",
  "components\charts\*.kt",
  "components\layouts\*.kt",
  "components\media\*.kt",
  "components\input\*.kt",
  "components\navigation\*.kt",
  "components\DatePicker.kt",
  "components\FilterChipGroup.kt",
  "components\FilePicker.kt",
  "components\ImagePicker.kt",
  "components\PdfViewer.kt",
  "components\WebViewScreen.kt",
  "components\SearchBar.kt"
)
$keep = @(
  "CommonComponents.kt","Dialogs.kt","FeedbackComponents.kt","Cards.kt","Lists.kt","Forms.kt",
  "CoreComponents.kt","Charts.kt","Layouts.kt","MediaComponents.kt","NavigationComponents.kt","InputComponents.kt"
)
Get-ChildItem -Path (Join-Path $root "components") -Recurse -Filter "*.kt" | ForEach-Object {
  $name = $_.Name
  if ($keep -contains $name) { return }
  $rel = $_.FullName.Replace((Join-Path $root "components") + "\", "")
  foreach ($pat in $patterns) {
    if ($rel -like $pat) { Remove-Item $_.FullName -Force; break }
  }
}
Write-Host "Cleanup complete"
