/*
 Navicat Premium Data Transfer

 Source Server         : Virtcon
 Source Server Type    : PostgreSQL
 Source Server Version : 120002
 Source Host           : localhost:5432
 Source Catalog        : nationsplus
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120002
 File Encoding         : 65001

 Date: 21/07/2022 18:08:12
*/


-- ----------------------------
-- Sequence structure for player_bans_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."player_bans_id_seq";
CREATE SEQUENCE "public"."player_bans_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."player_bans_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for nation
-- ----------------------------
DROP TABLE IF EXISTS "public"."nation";
CREATE TABLE "public"."nation" (
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "prefix" text COLLATE "pg_catalog"."default",
  "king_id" text COLLATE "pg_catalog"."default",
  "successor_id" text COLLATE "pg_catalog"."default",
  "created_date" timestamptz(6),
  "kills" int2 NOT NULL DEFAULT 0,
  "balance" numeric(18,2) NOT NULL DEFAULT 0,
  "tax" int2 NOT NULL DEFAULT 0,
  "x" int2,
  "y" int2,
  "z" int2
)
;
ALTER TABLE "public"."nation" OWNER TO "postgres";

-- ----------------------------
-- Table structure for nation_relations
-- ----------------------------
DROP TABLE IF EXISTS "public"."nation_relations";
CREATE TABLE "public"."nation_relations" (
  "nation_one" text COLLATE "pg_catalog"."default" NOT NULL,
  "nation_second" text COLLATE "pg_catalog"."default" NOT NULL,
  "status" text COLLATE "pg_catalog"."default" NOT NULL,
  "peace_available" bool,
  "wants_peace" text COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."nation_relations" OWNER TO "postgres";

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS "public"."player";
CREATE TABLE "public"."player" (
  "uid" text COLLATE "pg_catalog"."default" NOT NULL,
  "nation" text COLLATE "pg_catalog"."default",
  "kills" int4,
  "deaths" int2,
  "last_login" timestamptz(6),
  "player_name" text COLLATE "pg_catalog"."default" NOT NULL,
  "balance" numeric(255,0) DEFAULT 0
)
;
ALTER TABLE "public"."player" OWNER TO "postgres";

-- ----------------------------
-- Table structure for player_bans
-- ----------------------------
DROP TABLE IF EXISTS "public"."player_bans";
CREATE TABLE "public"."player_bans" (
  "player_id" text COLLATE "pg_catalog"."default" NOT NULL,
  "banned_date" timestamptz(6) NOT NULL,
  "banned_minutes" int4 NOT NULL,
  "id" int4 NOT NULL DEFAULT nextval('player_bans_id_seq'::regclass)
)
;
ALTER TABLE "public"."player_bans" OWNER TO "postgres";

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."player_bans_id_seq"
OWNED BY "public"."player_bans"."id";
SELECT setval('"public"."player_bans_id_seq"', 2, false);

-- ----------------------------
-- Primary Key structure for table nation
-- ----------------------------
ALTER TABLE "public"."nation" ADD CONSTRAINT "nation_pkey" PRIMARY KEY ("name");

-- ----------------------------
-- Primary Key structure for table nation_relations
-- ----------------------------
ALTER TABLE "public"."nation_relations" ADD CONSTRAINT "nation_relations_pkey" PRIMARY KEY ("nation_one", "nation_second");

-- ----------------------------
-- Primary Key structure for table player
-- ----------------------------
ALTER TABLE "public"."player" ADD CONSTRAINT "player_pkey" PRIMARY KEY ("uid");

-- ----------------------------
-- Primary Key structure for table player_bans
-- ----------------------------
ALTER TABLE "public"."player_bans" ADD CONSTRAINT "player_bans_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Keys structure for table nation
-- ----------------------------
ALTER TABLE "public"."nation" ADD CONSTRAINT "fk_kingId" FOREIGN KEY ("king_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."nation" ADD CONSTRAINT "fk_successorId" FOREIGN KEY ("successor_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table nation_relations
-- ----------------------------
ALTER TABLE "public"."nation_relations" ADD CONSTRAINT "fk_nation_one" FOREIGN KEY ("nation_one") REFERENCES "public"."nation" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."nation_relations" ADD CONSTRAINT "fk_nation_two" FOREIGN KEY ("nation_second") REFERENCES "public"."nation" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."nation_relations" ADD CONSTRAINT "nation_relations_wants_peace_fkey" FOREIGN KEY ("wants_peace") REFERENCES "public"."nation" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table player
-- ----------------------------
ALTER TABLE "public"."player" ADD CONSTRAINT "fk_nation" FOREIGN KEY ("nation") REFERENCES "public"."nation" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table player_bans
-- ----------------------------
ALTER TABLE "public"."player_bans" ADD CONSTRAINT "fk_banned_player" FOREIGN KEY ("player_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;
