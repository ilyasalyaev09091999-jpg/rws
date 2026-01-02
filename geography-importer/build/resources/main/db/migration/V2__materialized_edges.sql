CREATE MATERIALIZED VIEW public.edges_astar AS
SELECT
    e.id,
    e.source,
    e.target,
    e.cost,
    ST_X(n1.geom) AS x1,
    ST_Y(n1.geom) AS y1,
    ST_X(n2.geom) AS x2,
    ST_Y(n2.geom) AS y2,
    n1.geom AS geom1,
    n2.geom AS geom2
FROM edges e
JOIN nodes n1 ON e.source = n1.id
JOIN nodes n2 ON e.target = n2.id;

CREATE INDEX idx_edges_astar_id ON edges_astar (id);
CREATE INDEX idx_edges_astar_source ON edges_astar (source);
CREATE INDEX idx_edges_astar_target ON edges_astar (target);

CREATE INDEX idx_edges_astar_geom1 ON edges_astar USING gist (geom1);
CREATE INDEX idx_edges_astar_geom2 ON edges_astar USING gist (geom2);
