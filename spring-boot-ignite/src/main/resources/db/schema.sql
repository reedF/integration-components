CREATE SCHEMA IF NOT EXISTS ignite;

DROP TABLE IF EXISTS ignite.person;

CREATE TABLE ignite.person (
  id   BIGINT NOT NULL,
  name VARCHAR(255),
  male BOOLEAN,
  PRIMARY KEY (id)
)