DROP TABLE IF EXISTS tmp_connected_components;

CREATE TEMP TABLE tmp_connected_components AS
SELECT
    cc.node,
    cc.component
FROM pgr_connectedComponents(
    'SELECT id, source, target, COALESCE(cost, 1) AS cost, COALESCE(cost, 1) AS reverse_cost FROM edges'
) AS cc;

DROP TABLE IF EXISTS tmp_node_components;

CREATE TEMP TABLE tmp_node_components AS
SELECT
    n.id AS node_id,
    COALESCE(cc.component, n.id) AS component_id
FROM nodes n
LEFT JOIN tmp_connected_components cc ON cc.node = n.id;

DROP TABLE IF EXISTS tmp_edge_components;

CREATE TEMP TABLE tmp_edge_components AS
SELECT
    e.id AS edge_id,
    nc.component_id
FROM edges e
JOIN tmp_node_components nc ON nc.node_id = e.source;

DROP MATERIALIZED VIEW IF EXISTS edges_astar_new;

CREATE MATERIALIZED VIEW edges_astar_new AS
SELECT
    e.id,
    e.source,
    e.target,
    e.cost,
    tec.component_id,
    ST_X(n1.geom) AS x1,
    ST_Y(n1.geom) AS y1,
    ST_X(n2.geom) AS x2,
    ST_Y(n2.geom) AS y2,
    n1.geom AS geom1,
    n2.geom AS geom2
FROM edges e
JOIN tmp_edge_components tec ON tec.edge_id = e.id
JOIN nodes n1 ON e.source = n1.id
JOIN nodes n2 ON e.target = n2.id;

UPDATE nodes n
SET component_id = tnc.component_id
FROM tmp_node_components tnc
WHERE n.id = tnc.node_id
  AND n.component_id IS DISTINCT FROM tnc.component_id;

UPDATE edges e
SET component_id = tec.component_id
FROM tmp_edge_components tec
WHERE e.id = tec.edge_id
  AND e.component_id IS DISTINCT FROM tec.component_id;

DROP MATERIALIZED VIEW IF EXISTS edges_astar;

ALTER MATERIALIZED VIEW edges_astar_new RENAME TO edges_astar;

CREATE INDEX IF NOT EXISTS idx_edges_astar_id ON edges_astar (id);
CREATE INDEX IF NOT EXISTS idx_edges_astar_source ON edges_astar (source);
CREATE INDEX IF NOT EXISTS idx_edges_astar_target ON edges_astar (target);
CREATE INDEX IF NOT EXISTS idx_edges_astar_component_id ON edges_astar (component_id);
CREATE INDEX IF NOT EXISTS idx_edges_astar_component_source_target ON edges_astar (component_id, source, target);
CREATE INDEX IF NOT EXISTS idx_edges_astar_geom1 ON edges_astar USING gist (geom1);
CREATE INDEX IF NOT EXISTS idx_edges_astar_geom2 ON edges_astar USING gist (geom2);

INSERT INTO graph_version (id, version)
VALUES (1, 1)
ON CONFLICT (id) DO UPDATE
SET version = graph_version.version + 1;
