TRUNCATE TABLE edges RESTART IDENTITY;

INSERT INTO edges (source, target, cost, geom)
SELECT
    wn1.node_id AS source,
    wn2.node_id AS target,
    ST_Length(ST_MakeLine(n1.geom, n2.geom)::geography) AS cost,
    ST_MakeLine(n1.geom, n2.geom) AS geom
FROM way_nodes wn1
JOIN way_nodes wn2
  ON wn1.way_id = wn2.way_id
 AND wn2.sequence_index = wn1.sequence_index + 1
JOIN nodes n1 ON wn1.node_id = n1.id
JOIN nodes n2 ON wn2.node_id = n2.id;

REFRESH MATERIALIZED VIEW edges_astar;