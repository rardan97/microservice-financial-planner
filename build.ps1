$ErrorActionPreference = "Stop"

# Inisialisasi data
$pomFiles = Get-ChildItem -Filter "pom.xml" -Recurse
if ($pomFiles.Count -eq 0) {
    Write-Warning "Tidak ditemukan file pom.xml"
    exit 1
}

$successCount = 0
$failCount = 0
$failedServices = @()

foreach ($pom in $pomFiles) {
    $serviceDir = $pom.DirectoryName
    $serviceName = Split-Path $serviceDir -Leaf

    Write-Host "$serviceName - process build" -ForegroundColor Cyan

    Push-Location $serviceDir
    try {
        # Jalankan mvn dan sembunyikan output
        & mvn clean package -DskipTests *> $null

        Write-Host "$serviceName - success build" -ForegroundColor Green
        $successCount++
    } catch {
        Write-Host "$serviceName - failed build" -ForegroundColor Red
        $failCount++
        $failedServices += $serviceName
    }
    Pop-Location

    Write-Host ""
}

# Ringkasan akhir
Write-Host "===========================" -ForegroundColor Yellow
Write-Host "Build Summary:" -ForegroundColor Yellow
Write-Host "$successCount service berhasil dibuild" -ForegroundColor Green

if ($failCount -gt 0) {
    Write-Host "$failCount service gagal dibuild:" -ForegroundColor Red
    foreach ($fail in $failedServices) {
        Write-Host "   - $fail" -ForegroundColor Red
    }
    exit 1
} else {
    Write-Host "Semua service berhasil dibuild!" -ForegroundColor Yellow
}