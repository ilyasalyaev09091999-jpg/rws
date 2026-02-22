CREATE TABLE public.graph_version (
    id SERIAL PRIMARY KEY,          -- уникальный идентификатор записи
    version BIGINT NOT NULL         -- номер версии графа
);