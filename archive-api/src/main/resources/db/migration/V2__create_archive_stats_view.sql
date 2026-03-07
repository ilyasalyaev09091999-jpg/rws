CREATE OR REPLACE VIEW v_archive_route_stats AS
SELECT
    from_city,
    to_city,
    EXTRACT(MONTH FROM departure_date)::INT AS departure_month,
    COUNT(*)::BIGINT AS trips_count,
    MIN(duration_days)::INT AS min_days,
    MAX(duration_days)::INT AS max_days,
    ROUND(AVG(duration_days)::NUMERIC, 2) AS avg_days,
    ROUND(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY duration_days)::NUMERIC, 2) AS p50_days,
    ROUND(PERCENTILE_CONT(0.8) WITHIN GROUP (ORDER BY duration_days)::NUMERIC, 2) AS p80_days,
    0.5::NUMERIC(3,1) AS uncertainty_days
FROM archive_trip
GROUP BY from_city, to_city, EXTRACT(MONTH FROM departure_date)::INT;
