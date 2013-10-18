# Tasks schema
 
# --- !Ups

CREATE SEQUENCE user_id_seq;
CREATE TABLE user (
    id integer NOT NULL DEFAULT nextval('user_id_seq'),
    name varchar(255),
    pass varchar(255)
);
 
# --- !Downs
 
DROP TABLE user;
DROP SEQUENCE user_id_seq;
