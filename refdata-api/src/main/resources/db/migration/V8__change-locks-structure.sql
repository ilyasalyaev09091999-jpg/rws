ALTER TABLE locks DROP COLUMN node_id;

CREATE TABLE cameras (
	id bigserial PRIMARY KEY,

	lock_id varchar(64) NOT NULL,
	name varchar(255),

	latitude float8 NOT NULL,
	longitude float8 NOT NULL,

	edge_id bigint NOT NULL,
	direction boolean NOT NULL,
	-- direction:
	-- true  = source → target
	-- false = target → source

	CONSTRAINT fk_camera_lock
		FOREIGN KEY (lock_id)
		REFERENCES public.locks (id)
		ON DELETE CASCADE
);
