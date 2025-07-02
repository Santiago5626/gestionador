# Script para actualizar todas las referencias de Gestion a Gestionador en layouts
$layoutFiles = Get-ChildItem -Path "app/src/main/res/layout" -Filter "*.xml" -Recurse

foreach ($file in $layoutFiles) {
    $content = Get-Content $file.FullName -Raw
    $content = $content -replace "TextAppearance\.Gestion\.", "TextAppearance.Gestionador."
    $content = $content -replace "Widget\.Gestion\.", "Widget.Gestionador."
    Set-Content -Path $file.FullName -Value $content
    Write-Host "Updated: $($file.Name)"
}

Write-Host "All layout files updated successfully!"
