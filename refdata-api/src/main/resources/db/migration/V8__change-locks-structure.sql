ALTER TABLE locks DROP COLUMN IF EXISTS node_id;

CREATE TABLE public.lock_nodes (
    lock_id varchar(64) NOT NULL,
    node_id bigint NOT NULL,

    CONSTRAINT lock_nodes_pkey PRIMARY KEY (lock_id, node_id),
    CONSTRAINT lock_nodes_lock_fk
        FOREIGN KEY (lock_id)
        REFERENCES public.locks(id)
        ON DELETE CASCADE
);
