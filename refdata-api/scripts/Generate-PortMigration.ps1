param(
    [Parameter(Mandatory = $true)]
    [string]$InputFile,

    [string]$MigrationDescription = "insert-ports-generated",

    [string]$OutputFile,

    [string]$MigrationsDir,

    [string]$Delimiter = ";",

    [switch]$SkipExistingIds
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($MigrationsDir)) {
    $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
    $MigrationsDir = Join-Path $scriptDir "..\src\main\resources\db\migration"
}

function Get-NextMigrationVersion {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Directory
    )

    $versions = Get-ChildItem -Path $Directory -File |
        Where-Object { $_.Name -match '^V(\d+)__.*\.sql$' } |
        ForEach-Object { [int]$matches[1] }

    if (-not $versions) {
        return 1
    }

    return (($versions | Measure-Object -Maximum).Maximum + 1)
}

function Get-ExistingPortIds {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Directory
    )

    $ids = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
    $pattern = [regex]"\('([^']+)'\s*,"

    Get-ChildItem -Path $Directory -File |
        Where-Object { $_.Name -match '^V\d+__.*\.sql$' } |
        ForEach-Object {
            foreach ($line in Get-Content -Path $_.FullName -Encoding UTF8) {
                $match = $pattern.Match($line)
                if ($match.Success) {
                    [void]$ids.Add($match.Groups[1].Value)
                }
            }
        }

    return $ids
}

function Escape-SqlString {
    param(
        [AllowNull()]
        [string]$Value
    )

    if ($null -eq $Value) {
        return ""
    }

    return $Value.Replace("'", "''")
}

function Normalize-Decimal {
    param(
        [AllowNull()]
        [string]$Value
    )

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $null
    }

    return $Value.Trim().Replace(",", ".")
}

function Resolve-PortCoordinates {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$Row
    )

    $latitude = Normalize-Decimal $Row.latitude
    $longitude = Normalize-Decimal $Row.longitude

    if ($latitude -and $longitude) {
        return @{
            Latitude = [double]::Parse($latitude, [System.Globalization.CultureInfo]::InvariantCulture)
            Longitude = [double]::Parse($longitude, [System.Globalization.CultureInfo]::InvariantCulture)
            Source = "csv"
        }
    }

    $query = if (-not [string]::IsNullOrWhiteSpace($Row.query)) {
        $Row.query.Trim()
    } else {
        $Row.name.Trim()
    }

    if ([string]::IsNullOrWhiteSpace($query)) {
        throw "For rows without coordinates, fill at least 'query' or 'name'."
    }

    $uri = "https://nominatim.openstreetmap.org/search?format=jsonv2&limit=1&q=$([uri]::EscapeDataString($query))"
    $headers = @{
        "User-Agent" = "routing-waterway-system-port-importer/1.0"
        "Accept-Language" = "ru"
    }

    $response = Invoke-RestMethod -Uri $uri -Headers $headers -Method Get
    if (-not $response -or $response.Count -eq 0) {
        throw "Failed to resolve coordinates for '$query'."
    }

    return @{
        Latitude = [double]::Parse($response[0].lat, [System.Globalization.CultureInfo]::InvariantCulture)
        Longitude = [double]::Parse($response[0].lon, [System.Globalization.CultureInfo]::InvariantCulture)
        Source = "nominatim"
    }
}

if (-not (Test-Path -LiteralPath $InputFile)) {
    throw "Input file not found: $InputFile"
}

if (-not (Test-Path -LiteralPath $MigrationsDir)) {
    throw "Migrations directory not found: $MigrationsDir"
}

$rows = Import-Csv -Path $InputFile -Delimiter $Delimiter -Encoding UTF8
if (-not $rows -or $rows.Count -eq 0) {
    throw "Input file contains no rows."
}

$existingIds = Get-ExistingPortIds -Directory $MigrationsDir
$seenIds = [System.Collections.Generic.HashSet[string]]::new([System.StringComparer]::OrdinalIgnoreCase)
$generatedRows = [System.Collections.Generic.List[string]]::new()

foreach ($row in $rows) {
    if ([string]::IsNullOrWhiteSpace($row.id)) {
        throw "Each row must have 'id'."
    }

    if ([string]::IsNullOrWhiteSpace($row.name)) {
        throw "Each row must have 'name'."
    }

    $portId = $row.id.Trim()
    $portName = $row.name.Trim()

    if (-not $seenIds.Add($portId)) {
        throw "Duplicate id in input file: '$portId'."
    }

    if ($existingIds.Contains($portId)) {
        if ($SkipExistingIds) {
            Write-Host "Skipping '$portId': already present in migrations."
            continue
        }

        throw "Port id '$portId' already exists in migrations. Use -SkipExistingIds to ignore duplicates."
    }

    $coordinates = Resolve-PortCoordinates -Row $row

    $lat = $coordinates.Latitude.ToString("0.000000", [System.Globalization.CultureInfo]::InvariantCulture)
    $lon = $coordinates.Longitude.ToString("0.000000", [System.Globalization.CultureInfo]::InvariantCulture)
    $escapedId = Escape-SqlString $portId
    $escapedName = Escape-SqlString $portName

    $generatedRows.Add("('$escapedId', '$escapedName', $lat, $lon)")
    Write-Host "Prepared $portId -> $lat, $lon [$($coordinates.Source)]"
}

if ($generatedRows.Count -eq 0) {
    throw "No rows left to generate after filtering."
}

if ([string]::IsNullOrWhiteSpace($OutputFile)) {
    $version = Get-NextMigrationVersion -Directory $MigrationsDir
    $safeDescription = ($MigrationDescription.Trim().ToLowerInvariant() -replace '[^a-z0-9-]+', '-').Trim('-')
    if ([string]::IsNullOrWhiteSpace($safeDescription)) {
        $safeDescription = "insert-ports-generated"
    }

    $fileName = "V${version}__${safeDescription}.sql"
    $OutputFile = Join-Path $MigrationsDir $fileName
}

$sqlLines = [System.Collections.Generic.List[string]]::new()
$sqlLines.Add("INSERT INTO ports (id, name, latitude, longitude)")
$sqlLines.Add("VALUES")

for ($i = 0; $i -lt $generatedRows.Count; $i++) {
    $suffix = if ($i -eq $generatedRows.Count - 1) { ";" } else { "," }
    $sqlLines.Add($generatedRows[$i] + $suffix)
}

[System.IO.File]::WriteAllLines($OutputFile, $sqlLines, [System.Text.UTF8Encoding]::new($false))
Write-Host "Done: $OutputFile"
