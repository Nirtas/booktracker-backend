CREATE TABLE IF NOT EXISTS public.books
(
    book_id uuid NOT NULL,
    title character varying(500) NOT NULL,
    author character varying(500) NOT NULL,
    cover_url text,
    status text NOT NULL,
    created_at timestamp with time zone NOT NULL DEFAULT now(),
    CONSTRAINT books_pkey PRIMARY KEY (book_id)
);

CREATE TABLE IF NOT EXISTS public.genres
(
    genre_id serial NOT NULL,
    genre_name character varying(128) NOT NULL,
    CONSTRAINT genres_pkey PRIMARY KEY (genre_id),
    CONSTRAINT genres_genre_name_key UNIQUE (genre_name)
);

CREATE TABLE IF NOT EXISTS public.book_genres
(
    book_id uuid NOT NULL,
    genre_id integer NOT NULL,
    CONSTRAINT book_genres_pkey PRIMARY KEY (book_id, genre_id),
    CONSTRAINT book_genres_book_id_fkey FOREIGN KEY (book_id)
        REFERENCES public.books (book_id)
        ON DELETE CASCADE,
    CONSTRAINT book_genres_genre_id_fkey FOREIGN KEY (genre_id)
        REFERENCES public.genres (genre_id)
        ON DELETE CASCADE
);
