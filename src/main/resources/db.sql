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

 Date: 09/10/2022 19:36:51
*/


-- ----------------------------
-- Sequence structure for bank_loan_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."bank_loan_id_seq";
CREATE SEQUENCE "public"."bank_loan_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;
ALTER SEQUENCE "public"."bank_loan_id_seq" OWNER TO "postgres";

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
-- Table structure for bank
-- ----------------------------
DROP TABLE IF EXISTS "public"."bank";
CREATE TABLE "public"."bank" (
  "bank_name" text COLLATE "pg_catalog"."default" NOT NULL,
  "owner" text COLLATE "pg_catalog"."default" NOT NULL,
  "saving_interest" numeric(6,4),
  "balance" numeric(18,2) NOT NULL DEFAULT 0
)
;
ALTER TABLE "public"."bank" OWNER TO "postgres";

-- ----------------------------
-- Table structure for bank_account
-- ----------------------------
DROP TABLE IF EXISTS "public"."bank_account";
CREATE TABLE "public"."bank_account" (
  "bank_name" text COLLATE "pg_catalog"."default" NOT NULL,
  "player_id" text COLLATE "pg_catalog"."default" NOT NULL,
  "balance" numeric(18,2) NOT NULL
)
;
ALTER TABLE "public"."bank_account" OWNER TO "postgres";

-- ----------------------------
-- Table structure for bank_loan
-- ----------------------------
DROP TABLE IF EXISTS "public"."bank_loan";
CREATE TABLE "public"."bank_loan" (
  "bank_name" text COLLATE "pg_catalog"."default" NOT NULL,
  "player_id" text COLLATE "pg_catalog"."default" NOT NULL,
  "amount_total" numeric(18,2),
  "amount_paid" numeric(18,2),
  "interest_rate" numeric(6,2),
  "accepted" bool NOT NULL,
  "active" bool NOT NULL,
  "payments_left" int2,
  "payments_total" int2,
  "id" int4 NOT NULL DEFAULT nextval('bank_loan_id_seq'::regclass)
)
;
ALTER TABLE "public"."bank_loan" OWNER TO "postgres";

-- ----------------------------
-- Table structure for block_reinforcement
-- ----------------------------
DROP TABLE IF EXISTS "public"."block_reinforcement";
CREATE TABLE "public"."block_reinforcement" (
  "health" int2 NOT NULL,
  "reinforcement_type" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
  "nation" text COLLATE "pg_catalog"."default",
  "block_id" varchar(40) COLLATE "pg_catalog"."default" NOT NULL,
  "player_id" text COLLATE "pg_catalog"."default",
  "world" text COLLATE "pg_catalog"."default" NOT NULL,
  "block_type" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "reinforcement_mode" varchar(10) COLLATE "pg_catalog"."default"
)
;
ALTER TABLE "public"."block_reinforcement" OWNER TO "postgres";
COMMENT ON COLUMN "public"."block_reinforcement"."block_id" IS 'XYZ as a string';

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
  "balance" numeric(255,0) DEFAULT 0,
  "password" varchar(512) COLLATE "pg_catalog"."default",
  "cookie" varchar(255) COLLATE "pg_catalog"."default",
  "reinforcement_mode" varchar(10) COLLATE "pg_catalog"."default"
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
  "id" int4 NOT NULL DEFAULT nextval('player_bans_id_seq'::regclass),
  "ban_reason" text COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."player_bans" OWNER TO "postgres";

-- ----------------------------
-- Table structure for stripe_customer
-- ----------------------------
DROP TABLE IF EXISTS "public"."stripe_customer";
CREATE TABLE "public"."stripe_customer" (
  "customer_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "player_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL
)
;
ALTER TABLE "public"."stripe_customer" OWNER TO "postgres";

-- ----------------------------
-- Table structure for stripe_session
-- ----------------------------
DROP TABLE IF EXISTS "public"."stripe_session";
CREATE TABLE "public"."stripe_session" (
  "session_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "stripe_price_id" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "stripe_customer_id" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
  "amount_total" int4 NOT NULL,
  "status" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "created" timestamp(6)
)
;
ALTER TABLE "public"."stripe_session" OWNER TO "postgres";

-- ----------------------------
-- Table structure for webshop_item
-- ----------------------------
DROP TABLE IF EXISTS "public"."webshop_item";
CREATE TABLE "public"."webshop_item" (
  "product_name" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "display_name" text COLLATE "pg_catalog"."default" NOT NULL,
  "description" text COLLATE "pg_catalog"."default",
  "quantity" int2,
  "stripe_price_id" varchar(50) COLLATE "pg_catalog"."default",
  "price" numeric(18,2) NOT NULL,
  "discount" numeric(18,2)
)
;
ALTER TABLE "public"."webshop_item" OWNER TO "postgres";

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."bank_loan_id_seq"
OWNED BY "public"."bank_loan"."id";
SELECT setval('"public"."bank_loan_id_seq"', 6, true);

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."player_bans_id_seq"
OWNED BY "public"."player_bans"."id";
SELECT setval('"public"."player_bans_id_seq"', 15, true);

-- ----------------------------
-- Primary Key structure for table bank
-- ----------------------------
ALTER TABLE "public"."bank" ADD CONSTRAINT "bank_pkey" PRIMARY KEY ("bank_name");

-- ----------------------------
-- Primary Key structure for table bank_loan
-- ----------------------------
ALTER TABLE "public"."bank_loan" ADD CONSTRAINT "bank_loan_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table block_reinforcement
-- ----------------------------
ALTER TABLE "public"."block_reinforcement" ADD CONSTRAINT "block_reinforcement_pkey" PRIMARY KEY ("block_id", "world");

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
-- Primary Key structure for table stripe_customer
-- ----------------------------
ALTER TABLE "public"."stripe_customer" ADD CONSTRAINT "stripe_customer_pkey" PRIMARY KEY ("customer_id");

-- ----------------------------
-- Primary Key structure for table stripe_session
-- ----------------------------
ALTER TABLE "public"."stripe_session" ADD CONSTRAINT "stripe_session_pkey" PRIMARY KEY ("session_id");

-- ----------------------------
-- Uniques structure for table webshop_item
-- ----------------------------
ALTER TABLE "public"."webshop_item" ADD CONSTRAINT "webshop_item_stripe_price_id_key" UNIQUE ("stripe_price_id");

-- ----------------------------
-- Primary Key structure for table webshop_item
-- ----------------------------
ALTER TABLE "public"."webshop_item" ADD CONSTRAINT "webshop_items_pkey" PRIMARY KEY ("product_name");

-- ----------------------------
-- Foreign Keys structure for table bank
-- ----------------------------
ALTER TABLE "public"."bank" ADD CONSTRAINT "fk_owner" FOREIGN KEY ("owner") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table bank_account
-- ----------------------------
ALTER TABLE "public"."bank_account" ADD CONSTRAINT "bank_account_bank_name_fkey" FOREIGN KEY ("bank_name") REFERENCES "public"."bank" ("bank_name") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."bank_account" ADD CONSTRAINT "bank_account_player_id_fkey" FOREIGN KEY ("player_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Keys structure for table block_reinforcement
-- ----------------------------
ALTER TABLE "public"."block_reinforcement" ADD CONSTRAINT "nationfk" FOREIGN KEY ("nation") REFERENCES "public"."nation" ("name") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."block_reinforcement" ADD CONSTRAINT "playerfk" FOREIGN KEY ("player_id") REFERENCES "public"."player" ("uid") ON DELETE NO ACTION ON UPDATE NO ACTION;

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

-- ----------------------------
-- Foreign Keys structure for table stripe_session
-- ----------------------------
ALTER TABLE "public"."stripe_session" ADD CONSTRAINT "fk_stripe_customer_id" FOREIGN KEY ("stripe_customer_id") REFERENCES "public"."stripe_customer" ("customer_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."stripe_session" ADD CONSTRAINT "stripe_session_webshop_item_id_fkey" FOREIGN KEY ("stripe_price_id") REFERENCES "public"."webshop_item" ("stripe_price_id") ON DELETE NO ACTION ON UPDATE NO ACTION;
