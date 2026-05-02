ALTER TABLE public.nodes
ADD COLUMN IF NOT EXISTS component_id BIGINT;

ALTER TABLE public.edges
ADD COLUMN IF NOT EXISTS component_id BIGINT;

CREATE INDEX IF NOT EXISTS idx_nodes_component_id ON public.nodes (component_id);
CREATE INDEX IF NOT EXISTS idx_edges_component_id ON public.edges (component_id);
CREATE INDEX IF NOT EXISTS idx_edges_component_source_target ON public.edges (component_id, source, target);
