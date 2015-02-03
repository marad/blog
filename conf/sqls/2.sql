-- UPS
create table "ACCOUNTS" ("ID" BIGSERIAL NOT NULL PRIMARY KEY,"USERNAME" VARCHAR(50) NOT NULL,"PASSWORD" VARCHAR(80) NOT NULL);
alter table "POSTS" alter column "TITLE" type VARCHAR(250);
alter table "POSTS" alter column "EXTRACT" type TEXT;
alter table "POSTS" alter column "CONTENT" type TEXT;
alter table "TAGS" alter column "NAME" type VARCHAR(100);

-- DOWNS
drop table "ACCOUNTS";
alter table "POSTS" alter column "TITLE" type VARCHAR(254);
alter table "POSTS" alter column "EXTRACT" type VARCHAR(254);
alter table "POSTS" alter column "CONTENT" type VARCHAR(254);
alter table "TAGS" alter column "NAME" type VARCHAR(254);
