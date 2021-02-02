CREATE USER metadata_simple IDENTIFIED BY "BOpVnzFi9Ew=1";
GRANT CREATE TABLE TO metadata_simple;
GRANT CREATE SESSION TO metadata_simple;

ALTER SESSION SET CURRENT_SCHEMA = METADATA_SIMPLE;

CREATE TABLE catalogue_item
(
  id            ROWID        NOT NULL
    CONSTRAINT catalogue_item_pkey
    PRIMARY KEY,
  version       NUMBER(19)   NOT NULL,
  date_created  TIMESTAMP    NOT NULL,
  domain_type   VARCHAR(255) NOT NULL,
  last_updated  TIMESTAMP    NOT NULL,
  path          CLOB         NOT NULL,
  depth         INTEGER      NOT NULL,
  created_by_id ROWID        NOT NULL,
  label         CLOB         NOT NULL,
  description   CLOB
);

CREATE INDEX catalogue_item_domain_type_idx
  ON catalogue_item (domain_type);

CREATE INDEX catalogue_item_created_by_idx
  ON catalogue_item (created_by_id);

CREATE TABLE catalogue_user
(
  id               ROWID        NOT NULL
    CONSTRAINT catalogue_user_pkey
    PRIMARY KEY,
  version          NUMBER(19)   NOT NULL,
  salt             BLOB         NOT NULL,
  date_created     TIMESTAMP    NOT NULL,
  first_name       VARCHAR(255) NOT NULL,
  domain_type      VARCHAR(255) NOT NULL,
  last_updated     TIMESTAMP    NOT NULL,
  organisation     VARCHAR(255),
  user_role        VARCHAR(255) NOT NULL,
  job_title        VARCHAR(255),
  email_address    VARCHAR(255) NOT NULL
    CONSTRAINT uk_26qjnuqu76954q376opkqelqd
    UNIQUE,
  user_preferences VARCHAR(255),
  password         BLOB,
  created_by_id    ROWID
    CONSTRAINT fk3s09b1t9lwqursuetowl2bi9t
    REFERENCES catalogue_user,
  temp_password    VARCHAR(255),
  last_name        VARCHAR(255) NOT NULL,
  last_login       TIMESTAMP,
  disabled         CHAR(1)
);

CREATE INDEX catalogue_user_created_by_idx
  ON catalogue_user (created_by_id);

ALTER TABLE catalogue_item
  ADD CONSTRAINT fkf9kx3d90ixy5pqc1d6kqgjui7
FOREIGN KEY (created_by_id) REFERENCES catalogue_user;

CREATE TABLE metadata
(
  id                ROWID        NOT NULL
    CONSTRAINT metadata_pkey
    PRIMARY KEY,
  version           NUMBER(19)   NOT NULL,
  date_created      TIMESTAMP    NOT NULL,
  domain_type       VARCHAR(255) NOT NULL,
  catalogue_item_id ROWID        NOT NULL
    CONSTRAINT fkk26px3s00mg783vb5gomhsw07
    REFERENCES catalogue_item,
  last_updated      TIMESTAMP    NOT NULL,
  namespace         VARCHAR(255) NOT NULL,
  value             CLOB         NOT NULL,
  created_by_id     ROWID        NOT NULL
    CONSTRAINT fkfo9b0grugrero8q84mxjst7jr
    REFERENCES catalogue_user,
  key               VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX unique_item_id_namespace_key
  ON metadata (catalogue_item_id, namespace, key);

CREATE INDEX metadata_catalogue_item_idx
  ON metadata (catalogue_item_id);

CREATE INDEX metadata_created_by_idx
  ON metadata (created_by_id);

