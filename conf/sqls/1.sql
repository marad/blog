-- UPS
create table "POSTS" ("ID" BIGSERIAL NOT NULL PRIMARY KEY,"TITLE" VARCHAR(254) NOT NULL,"EXTRACT" VARCHAR(254) NOT NULL,"CONTENT" VARCHAR(254) NOT NULL,"CREATED" TIMESTAMP NOT NULL,"UPDATED" TIMESTAMP NOT NULL);
create table "TAGS" ("ID" BIGSERIAL NOT NULL PRIMARY KEY,"NAME" VARCHAR(254) NOT NULL);
create unique index "NAME_IS_UNIQUE" on "TAGS" ("NAME");
create table "POST_TAGS" ("POST_ID" BIGINT NOT NULL,"TAG_ID" BIGINT NOT NULL);
create unique index "INDEX" on "POST_TAGS" ("POST_ID","TAG_ID");
alter table "POST_TAGS" add constraint "POST_FK" foreign key("POST_ID") references "POSTS"("ID") on update NO ACTION on delete CASCADE;
alter table "POST_TAGS" add constraint "TAG_FK" foreign key("TAG_ID") references "TAGS"("ID") on update NO ACTION on delete CASCADE;

-- DOWNS
alter table "POST_TAGS" drop constraint "POST_FK";
alter table "POST_TAGS" drop constraint "TAG_FK";
drop table "POST_TAGS";
drop table "TAGS";
drop table "POSTS";
