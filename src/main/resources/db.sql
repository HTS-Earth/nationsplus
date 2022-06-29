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

 Date: 29/06/2022 23:18:42
*/


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
  "tax" int2 NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."nation" OWNER TO "postgres";

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
-- Primary Key structure for table nation
-- ----------------------------
ALTER TABLE "public"."nation" ADD CONSTRAINT "nation_pkey" PRIMARY KEY ("name");

-- ----------------------------
-- Primary Key structure for table player
-- ----------------------------
ALTER TABLE "public"."player" ADD CONSTRAINT "player_pkey" PRIMARY KEY ("uid");

-- ----------------------------
-- Foreign Keys structure for table nation
-- ----------------------------
ALTER TABLE "public"."nation" ADD CONSTRAINT "fk_kingId" FOREIGN KEY ("king_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."nation" ADD CONSTRAINT "fk_successorId" FOREIGN KEY ("successor_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table player
-- ----------------------------
ALTER TABLE "public"."player" ADD CONSTRAINT "fk_nation" FOREIGN KEY ("nation") REFERENCES "public"."nation" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;
