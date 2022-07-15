--
-- PostgreSQL database dump
--

-- Dumped from database version 14.3
-- Dumped by pg_dump version 14.0

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: nationsplus; Type: DATABASE; Schema: -; Owner: postgres
--

CREATE DATABASE nationsplus WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE = 'en_US.UTF-8';


ALTER DATABASE nationsplus OWNER TO postgres;

\connect nationsplus

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: nation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nation (
    name text NOT NULL,
    prefix text,
    king_id text,
    successor_id text,
    created_date timestamp(6) with time zone,
    kills smallint DEFAULT 0 NOT NULL,
    balance numeric(18,2) DEFAULT 0 NOT NULL,
    tax smallint DEFAULT 0 NOT NULL,
    x smallint,
    y smallint,
    z smallint
);


ALTER TABLE public.nation OWNER TO postgres;

--
-- Name: nation_relations; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nation_relations (
    nation_one text NOT NULL,
    nation_second text NOT NULL,
    status text NOT NULL,
    peace_available boolean,
    wants_peace text
);


ALTER TABLE public.nation_relations OWNER TO postgres;

--
-- Name: player; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.player (
    uid text NOT NULL,
    nation text,
    kills integer,
    deaths smallint,
    last_login timestamp(6) with time zone,
    player_name text NOT NULL,
    balance numeric(255,0) DEFAULT 0
);


ALTER TABLE public.player OWNER TO postgres;

--
-- Name: player_bans; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.player_bans (
    player_id text NOT NULL,
    banned_date timestamp(6) with time zone NOT NULL,
    banned_minutes integer NOT NULL,
    id integer NOT NULL
);


ALTER TABLE public.player_bans OWNER TO postgres;

--
-- Name: player_bans_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.player_bans_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.player_bans_id_seq OWNER TO postgres;

--
-- Name: player_bans_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.player_bans_id_seq OWNED BY public.player_bans.id;


--
-- Name: player_bans id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player_bans ALTER COLUMN id SET DEFAULT nextval('public.player_bans_id_seq'::regclass);


--
-- Data for Name: nation; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nation (name, prefix, king_id, successor_id, created_date, kills, balance, tax, x, y, z) FROM stdin;
Sweden	SWE	e41696f9-69ae-4998-a71d-88d2c231646e	\N	2022-07-08 08:02:22.638109+02	0	0.00	0	\N	\N	\N
Turkey	TUR	e41696f9-69ae-4998-a71d-88d2c231646e	\N	2022-07-08 08:02:57.001755+02	0	0.00	0	\N	\N	\N
\.


--
-- Data for Name: nation_relations; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nation_relations (nation_one, nation_second, status, peace_available, wants_peace) FROM stdin;
Sweden	Turkey	war	\N	\N
\.


--
-- Data for Name: player; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.player (uid, nation, kills, deaths, last_login, player_name, balance) FROM stdin;
e41696f9-69ae-4998-a71d-88d2c231646e	Sweden	0	0	2022-07-15 17:40:46.925255+02	tackagud	0
\.


--
-- Data for Name: player_bans; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.player_bans (player_id, banned_date, banned_minutes, id) FROM stdin;
e41696f9-69ae-4998-a71d-88d2c231646e	2022-07-11 00:00:00+02	24	1
\.


--
-- Name: player_bans_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.player_bans_id_seq', 1, true);


--
-- Name: nation nation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT nation_pkey PRIMARY KEY (name);


--
-- Name: nation_relations nation_relations_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation_relations
    ADD CONSTRAINT nation_relations_pkey PRIMARY KEY (nation_one, nation_second);


--
-- Name: player_bans player_bans_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player_bans
    ADD CONSTRAINT player_bans_pkey PRIMARY KEY (id);


--
-- Name: player player_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT player_pkey PRIMARY KEY (uid);


--
-- Name: player_bans fk_banned_player; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player_bans
    ADD CONSTRAINT fk_banned_player FOREIGN KEY (player_id) REFERENCES public.player(uid);


--
-- Name: nation fk_kingId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT "fk_kingId" FOREIGN KEY (king_id) REFERENCES public.player(uid);


--
-- Name: player fk_nation; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.player
    ADD CONSTRAINT fk_nation FOREIGN KEY (nation) REFERENCES public.nation(name);


--
-- Name: nation_relations fk_nation_one; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation_relations
    ADD CONSTRAINT fk_nation_one FOREIGN KEY (nation_one) REFERENCES public.nation(name);


--
-- Name: nation_relations fk_nation_two; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation_relations
    ADD CONSTRAINT fk_nation_two FOREIGN KEY (nation_second) REFERENCES public.nation(name);


--
-- Name: nation fk_successorId; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation
    ADD CONSTRAINT "fk_successorId" FOREIGN KEY (successor_id) REFERENCES public.player(uid);


--
-- Name: nation_relations nation_relations_wants_peace_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nation_relations
    ADD CONSTRAINT nation_relations_wants_peace_fkey FOREIGN KEY (wants_peace) REFERENCES public.nation(name);


--
-- PostgreSQL database dump complete
--

