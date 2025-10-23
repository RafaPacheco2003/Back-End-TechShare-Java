# Run maven wrapper and collect logs for analysis
# Usage: Open PowerShell in the repo root and run: .\scripts\run_maven_and_collect_logs.ps1 -Tests "test,anotherTest"
param(
    [string]$MavenArgs = 'test',
    [string]$OutLog = 'build_full.log'
)

$ErrorActionPreference = 'Stop'

Write-Host "Running .\mvnw.cmd $MavenArgs ..."

# Save environment info
"=== ENV INFO ===" | Out-File -FilePath env_info.txt -Encoding utf8
try {
    java -version 2>&1 | Out-File -FilePath env_info.txt -Append -Encoding utf8
} catch {
    "java not found or failed to run" | Out-File -FilePath env_info.txt -Append -Encoding utf8
}

try {
    .\mvnw.cmd -v 2>&1 | Out-File -FilePath env_info.txt -Append -Encoding utf8
} catch {
    "mvnw failed to run - ensure mvnw.cmd exists" | Out-File -FilePath env_info.txt -Append -Encoding utf8
}

"=== GIT STATUS ===" | Out-File -FilePath env_info.txt -Append -Encoding utf8
git status --porcelain=v1 -b 2>&1 | Out-File -FilePath env_info.txt -Append -Encoding utf8

# Run maven and capture output
try {
    .\mvnw.cmd $MavenArgs 2>&1 | Tee-Object -FilePath $OutLog
} catch {
    Write-Host "Maven command failed. See $OutLog and env_info.txt"
    exit 1
}

Write-Host "Done. Logs: $OutLog, env_info.txt"
