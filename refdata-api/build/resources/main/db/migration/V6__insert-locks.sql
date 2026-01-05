ALTER TABLE locks ADD COLUMN node_id int8 NOT NULL;

INSERT INTO locks(id, name, latitude, longitude, node_id)
VALUES
('lock_n30', 'Шлюз № 30', 48.819722, 44.697865, 1343219331);