-- UPS
alter table "POSTS" add column "PUBLISHED" BOOLEAN DEFAULT FALSE;
update "POSTS" set "PUBLISHED" = true;

-- DOWNS
alter table "POSTS" drop column "PUBLISHED";
