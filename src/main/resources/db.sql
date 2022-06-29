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

 Date: 26/06/2022 21:59:26
*/


-- ----------------------------
-- Sequence structure for nation_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."nation_id_seq";
CREATE SEQUENCE "public"."nation_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."nation_id_seq" OWNER TO "postgres";

-- ----------------------------
-- Table structure for nation
-- ----------------------------
DROP TABLE IF EXISTS "public"."nation";
CREATE TABLE "public"."nation" (
  "id" int4 NOT NULL GENERATED ALWAYS AS IDENTITY (
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
),
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "prefix" text COLLATE "pg_catalog"."default",
  "kingId" text COLLATE "pg_catalog"."default",
  "successorId" text COLLATE "pg_catalog"."default",
  "createdDate" timestamp(6),
  "kills" int2 NOT NULL DEFAULT 0,
  "balance" numeric(255,15) NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."nation" OWNER TO "postgres";

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS "public"."player";
CREATE TABLE "public"."player" (
  "uid" text COLLATE "pg_catalog"."default" NOT NULL,
  "nationId" int4,
  "kills" int4,
  "deaths" int2
)
;
ALTER TABLE "public"."player" OWNER TO "postgres";

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."nation_id_seq"
OWNED BY "public"."nation"."id";
SELECT setval('"public"."nation_id_seq"', 2, false);

-- ----------------------------
-- Primary Key structure for table nation
-- ----------------------------
ALTER TABLE "public"."nation" ADD CONSTRAINT "nation_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table player
-- ----------------------------
ALTER TABLE "public"."player" ADD CONSTRAINT "player_pkey" PRIMARY KEY ("uid");

-- ----------------------------
-- Foreign Keys structure for table nation
-- ----------------------------
ALTER TABLE "public"."nation" ADD CONSTRAINT "fk_kingId" FOREIGN KEY ("kingId") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."nation" ADD CONSTRAINT "fk_successorId" FOREIGN KEY ("successorId") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table player
-- ----------------------------
ALTER TABLE "public"."player" ADD CONSTRAINT "fk_nationsId" FOREIGN KEY ("nationId") REFERENCES "public"."nation" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE "public"."player"
  ADD COLUMN "last_login" timestamp(255)