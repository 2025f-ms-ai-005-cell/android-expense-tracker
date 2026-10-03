$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force -Path (Join-Path $PSScriptRoot 'src'),(Join-Path $PSScriptRoot 'tests') | Out-Null
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'MainActivity.kt'),(Join-Path $PSScriptRoot 'Expense.kt') -Destination (Join-Path $PSScriptRoot 'src')
Copy-Item -LiteralPath (Join-Path $PSScriptRoot 'ExpenseRulesTest.kt') -Destination (Join-Path $PSScriptRoot 'tests')
Write-Output 'Sources prepared. Open this directory in Android Studio or use Gradle 8.13 with JDK 17+.'
