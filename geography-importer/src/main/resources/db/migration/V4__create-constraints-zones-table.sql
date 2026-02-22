CREATE TABLE constraints_zones (
    id BIGSERIAL PRIMARY KEY,
    name TEXT,
    min_depth NUMERIC,
    min_width NUMERIC,
    forecast_depth NUMERIC,
    geom GEOMETRY(LineString, 4326)  -- участок пути
);
