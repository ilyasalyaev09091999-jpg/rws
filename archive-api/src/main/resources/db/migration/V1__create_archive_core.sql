CREATE TABLE IF NOT EXISTS archive_trip (
    id BIGSERIAL PRIMARY KEY,
    source_file_name TEXT NOT NULL,
    source_row_num INTEGER NOT NULL,
    source_system TEXT NOT NULL DEFAULT 'xlsx',
    region_name TEXT,
    vessel_name TEXT,
    vessel_type TEXT,
    from_city TEXT NOT NULL,
    to_city TEXT NOT NULL,
    departure_date DATE NOT NULL,
    arrival_date DATE NOT NULL,
    duration_days INTEGER GENERATED ALWAYS AS (arrival_date - departure_date) STORED,
    cargo_type TEXT,
    quality_note TEXT,
    raw_payload JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_archive_trip_dates CHECK (arrival_date >= departure_date),
    CONSTRAINT uq_archive_trip_source UNIQUE (source_file_name, source_row_num)
);

CREATE TABLE IF NOT EXISTS archive_trip_port_resolution (
    id BIGSERIAL PRIMARY KEY,
    trip_id BIGINT NOT NULL REFERENCES archive_trip(id) ON DELETE CASCADE,
    point_type TEXT NOT NULL,
    city_name TEXT NOT NULL,
    port_id BIGINT,
    port_name TEXT,
    resolution_method TEXT NOT NULL,
    confidence NUMERIC(4,3),
    resolution_note TEXT,
    resolved_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_archive_trip_port_resolution_point_type
        CHECK (point_type IN ('DEPARTURE', 'ARRIVAL')),
    CONSTRAINT chk_archive_trip_port_resolution_method
        CHECK (resolution_method IN ('EXACT', 'PROBABLE', 'MANUAL', 'UNKNOWN')),
    CONSTRAINT chk_archive_trip_port_resolution_confidence
        CHECK (confidence IS NULL OR (confidence >= 0 AND confidence <= 1)),
    CONSTRAINT uq_archive_trip_port_resolution_trip_point UNIQUE (trip_id, point_type)
);

CREATE TABLE IF NOT EXISTS archive_trip_replay (
    trip_id BIGINT PRIMARY KEY REFERENCES archive_trip(id) ON DELETE CASCADE,
    start_lat DOUBLE PRECISION,
    start_lon DOUBLE PRECISION,
    end_lat DOUBLE PRECISION,
    end_lon DOUBLE PRECISION,
    replay_route JSONB NOT NULL,
    total_distance_km DOUBLE PRECISION,
    replay_source TEXT NOT NULL DEFAULT 'ROUTING_ENGINE',
    quality_label TEXT NOT NULL DEFAULT 'PROBABLE',
    generated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_archive_trip_replay_source
        CHECK (replay_source IN ('ROUTING_ENGINE', 'MANUAL')),
    CONSTRAINT chk_archive_trip_replay_quality
        CHECK (quality_label IN ('EXACT', 'PROBABLE', 'LOW'))
);

CREATE INDEX IF NOT EXISTS idx_archive_trip_route_filter
    ON archive_trip (from_city, to_city, departure_date);

CREATE INDEX IF NOT EXISTS idx_archive_trip_departure_month
    ON archive_trip ((EXTRACT(MONTH FROM departure_date)));

CREATE INDEX IF NOT EXISTS idx_archive_trip_port_resolution_lookup
    ON archive_trip_port_resolution (city_name, point_type, resolution_method);
