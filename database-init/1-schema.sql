DROP TABLE IF EXISTS public.genres CASCADE;

CREATE TABLE IF NOT EXISTS public.genres
(
    genre_id serial NOT NULL,
    genre_name text NOT NULL,
    CONSTRAINT genres_pkey PRIMARY KEY (genre_id),
    CONSTRAINT genres_genre_name_key UNIQUE (genre_name)
);
