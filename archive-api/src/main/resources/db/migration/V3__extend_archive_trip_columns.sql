ALTER TABLE archive_trip
    ADD COLUMN IF NOT EXISTS voyage_name TEXT,
    ADD COLUMN IF NOT EXISTS trip_type TEXT,
    ADD COLUMN IF NOT EXISTS tug_name TEXT,
    ADD COLUMN IF NOT EXISTS cargo_amount NUMERIC(18,3),
    ADD COLUMN IF NOT EXISTS draft_m NUMERIC(10,3),
    ADD COLUMN IF NOT EXISTS counterparty_name TEXT,
    ADD COLUMN IF NOT EXISTS counterparty_inn TEXT,
    ADD COLUMN IF NOT EXISTS flag TEXT,
    ADD COLUMN IF NOT EXISTS units_count INTEGER,
    ADD COLUMN IF NOT EXISTS region_from TEXT,
    ADD COLUMN IF NOT EXISTS region_to TEXT;

UPDATE archive_trip
SET
    voyage_name = COALESCE(voyage_name, vessel_name),
    trip_type = COALESCE(trip_type, vessel_type),
    region_from = COALESCE(region_from, region_name),
    region_to = COALESCE(region_to, region_name)
WHERE voyage_name IS NULL
   OR trip_type IS NULL
   OR region_from IS NULL
   OR region_to IS NULL;

ALTER TABLE archive_trip
    DROP COLUMN IF EXISTS region_name,
    DROP COLUMN IF EXISTS vessel_name,
    DROP COLUMN IF EXISTS vessel_type,
    DROP COLUMN IF EXISTS quality_note,
    DROP COLUMN IF EXISTS raw_payload;

CREATE INDEX IF NOT EXISTS idx_archive_trip_counterparty_inn
    ON archive_trip (counterparty_inn);


DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'archive_trip'
          AND column_name = 'from_city'
    ) THEN
        ALTER TABLE archive_trip RENAME COLUMN from_city TO departure_point;
    END IF;

    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'archive_trip'
          AND column_name = 'to_city'
    ) THEN
        ALTER TABLE archive_trip RENAME COLUMN to_city TO destination_point;
    END IF;
END $$;

DROP INDEX IF EXISTS idx_archive_trip_route_filter;
CREATE INDEX IF NOT EXISTS idx_archive_trip_route_filter
    ON archive_trip (departure_point, destination_point, departure_date);

DROP VIEW IF EXISTS v_archive_route_stats;
CREATE OR REPLACE VIEW v_archive_route_stats AS
SELECT
    departure_point,
    destination_point,
    EXTRACT(MONTH FROM departure_date)::INT AS departure_month,
    COUNT(*)::BIGINT AS trips_count,
    MIN(duration_days)::INT AS min_days,
    MAX(duration_days)::INT AS max_days,
    ROUND(AVG(duration_days)::NUMERIC, 2) AS avg_days,
    ROUND(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY duration_days)::NUMERIC, 2) AS p50_days,
    ROUND(PERCENTILE_CONT(0.8) WITHIN GROUP (ORDER BY duration_days)::NUMERIC, 2) AS p80_days,
    0.5::NUMERIC(3,1) AS uncertainty_days
FROM archive_trip
GROUP BY departure_point, destination_point, EXTRACT(MONTH FROM departure_date)::INT;