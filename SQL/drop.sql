alter table "NEWS_ITEMS" drop constraint "USER_NEWS_FK";
alter table "SSMU_PROFILES" drop constraint "USER_PROFILE_FK";
alter table "SSMU_RECORDS" drop constraint "USER_RECORD_FK";
drop table "NEWS_ITEMS";
drop table "SSMU_USERS";
drop table "SSMU_PROFILES";
drop table "SSMU_RECORDS"