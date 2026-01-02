-- Таблица нод
CREATE TABLE public.nodes (
	id int8 NOT NULL,
	latitude float8 NOT NULL,
	longitude float8 NOT NULL,
	geom geometry(point, 4326) NOT NULL,
	CONSTRAINT nodes_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_nodes_geom ON public.nodes USING gist (geom);

-- Таблица путей
CREATE TABLE public.ways (
	id int8 NOT NULL,
	CONSTRAINT ways_pkey PRIMARY KEY (id)
);

-- Таблица связи ManyToMany
CREATE TABLE public.way_nodes (
	way_id int8 NOT NULL,
	node_id int8 NOT NULL,
	sequence_index int4 NOT NULL,
	CONSTRAINT way_nodes_pkey PRIMARY KEY (way_id, sequence_index),
	CONSTRAINT fk_node FOREIGN KEY (node_id) REFERENCES public.nodes(id) ON DELETE CASCADE,
	CONSTRAINT fk_way FOREIGN KEY (way_id) REFERENCES public.ways(id) ON DELETE CASCADE
);
CREATE INDEX idx_way_nodes_node_id ON public.way_nodes USING btree (node_id);
CREATE INDEX idx_way_nodes_way_id ON public.way_nodes USING btree (way_id);

-- Таблица ребер графа
CREATE TABLE public.edges (
	"source" int8 NOT NULL,
	target int8 NOT NULL,
	id bigserial NOT NULL,
	"cost" float8 NULL,
	geom public.geometry(linestring, 4326) NOT NULL,
	CONSTRAINT edges_pkey PRIMARY KEY (id)
);
CREATE INDEX idx_edges_geom ON public.edges USING gist (geom);
CREATE INDEX idx_edges_source ON public.edges USING btree (source);
CREATE INDEX idx_edges_target ON public.edges USING btree (target);