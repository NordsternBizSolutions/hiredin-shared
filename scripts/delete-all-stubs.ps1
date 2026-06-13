# Delete all Kotlin files that are still stubs
$root = "c:\Users\My Computer\AndroidStudioProjects\hiredinshared\shared\src\main\java\com\nordstern\hiredin\shared"
$deleted = 0
Get-ChildItem -Path $root -Recurse -Filter "*.kt" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    if ($content -match "// Stub implementation" -or $content -match "fun Placeholder\(\)") {
        Remove-Item $_.FullName -Force
        $deleted++
    }
}
Write-Host "Deleted $deleted stub files"
