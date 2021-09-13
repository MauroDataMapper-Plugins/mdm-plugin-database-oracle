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

CREATE TABLE organisation
(
  id INT NOT NULL PRIMARY KEY,
  org_name VARCHAR2(255) NOT NULL,
  org_type VARCHAR2(20) NOT NULL,
  org_code VARCHAR2(15) NOT NULL,
  description VARCHAR2(2000),
  org_char CHAR(5)
);

GRANT UNLIMITED TABLESPACE TO metadata_simple;
GRANT INSERT ON organisation TO metadata_simple;

--Use both VARCHAR and CHAR columns. Expect that because there are no CHAR columns other than org_char,
--when org_char is detected as an enumeration, CHAR will be removed from the primitive data types
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (1, 'ORG1', 'TYPEA', 'CODEY', 'Description of ORG1', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (2, 'ORG2', 'TYPEA', 'CODEY', 'Description of ORG2', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (3, 'ORG3', 'TYPEA', 'CODEY', 'Description of ORG3', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (4, 'ORG4', 'TYPEA', 'CODEY', 'Description of ORG4', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (5, 'ORG5', 'TYPEB', 'CODEY', 'Description of ORG5', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (6, 'ORG6', 'TYPEB', 'CODEY', 'Description of ORG6', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (7, 'ORG7', 'TYPEB', 'CODEY', 'Description of ORG7', 'CHAR1');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (8, 'ORG8', 'TYPEB', 'CODEY', 'Description of ORG8', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (9, 'ORG9', 'TYPEB', 'CODEY', 'Description of ORG9', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (10, 'ORG10', 'TYPEA', 'CODEZ', 'Description of ORG10', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (11, 'ORG11', 'TYPEA', 'CODEZ', 'Description of ORG11', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (12, 'ORG12', 'TYPEA', 'CODEZ', 'Description of ORG12', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (13, 'ORG13', 'TYPEA', 'CODEZ', 'Description of ORG13', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (14, 'ORG14', 'TYPEA', 'CODEZ', 'Description of ORG14', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (15, 'ORG15', 'TYPEB', 'CODEZ', 'Description of ORG15', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (16, 'ORG16', 'TYPEB', 'CODEZ', 'Description of ORG16', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (17, 'ORG17', 'TYPEB', 'CODEZ', 'Description of ORG17', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (18, 'ORG18', 'TYPEB', 'CODEZ', 'Description of ORG18', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (19, 'ORG19', 'TYPEB', 'CODEZ', 'Description of ORG19', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (20, 'ORG20', 'TYPEB', 'CODEZ', 'Description of ORG20', 'CHAR2');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (21, 'ORG21', 'TYPEA', 'CODEX', 'Description of ORG21', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (22, 'ORG22', 'TYPEA', 'CODEX', 'Description of ORG22', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (23, 'ORG23', 'TYPEA', 'CODEX', 'Description of ORG23', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (24, 'ORG24', 'TYPEA', 'CODEX', 'Description of ORG24', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (25, 'ORG25', 'TYPEB', 'CODEX', 'Description of ORG25', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (26, 'ORG26', 'TYPEB', 'CODEX', 'Description of ORG26', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (27, 'ORG27', 'TYPEB', 'CODEX', 'Description of ORG27', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (28, 'ORG28', 'TYPEB', 'CODEX', 'Description of ORG28', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (29, 'ORG29', 'TYPEB', 'CODEX', 'Description of ORG29', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (30, 'ORG30', 'TYPEB', 'CODEX', 'Description of ORG30', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (31, 'ORG31', 'TYPEA', 'CODEX', 'Description of ORG31', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (32, 'ORG32', 'TYPEA', 'CODEX', 'Description of ORG32', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (33, 'ORG33', 'TYPEA', 'CODEX', 'Description of ORG33', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (34, 'ORG34', 'TYPEA', 'CODEX', 'Description of ORG34', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (35, 'ORG35', 'TYPEC', 'CODEX', 'Description of ORG35', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (36, 'ORG36', 'TYPEC', 'CODEX', 'Description of ORG36', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (37, 'ORG37', 'TYPEB', 'CODEX', 'Description of ORG37', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (38, 'ORG38', 'TYPEB', 'CODEX', 'Description of ORG38', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (39, 'ORG39', 'TYPEB', 'CODEX', 'Description of ORG39', 'CHAR3');
INSERT INTO organisation(id, org_name, org_type, org_code, description, org_char) VALUES (40, 'ORG40', 'TYPEB', 'CODER', 'Description of ORG40', 'CHAR3');

CREATE TABLE sample
(
  id INT NOT NULL PRIMARY KEY,
  sample_smallint SMALLINT,
  sample_int INT,
  sample_decimal DECIMAL(12,3),
  sample_numeric NUMERIC(10,6),
  sample_date DATE
);

INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (1, -100 ,10000, 5730000, -9.545940104037, TO_DATE('01/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (2, -99 ,9801, 5615973, -9.262416137007, TO_DATE('02/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (3, -98 ,9604, 5503092, -8.98456245839879, TO_DATE('03/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (4, -97 ,9409, 5391357, -8.71232179257176, TO_DATE('04/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (5, -96 ,9216, 5280768, -8.44563686388528, TO_DATE('05/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (6, -95 ,9025, 5171325, -8.18445039669872, TO_DATE('06/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (7, -94 ,8836, 5063028, -7.92870511537147, TO_DATE('07/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (8, -93 ,8649, 4955877, -7.67834374426289, TO_DATE('08/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (9, -92 ,8464, 4849872, -7.43330900773236, TO_DATE('09/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (10, -91 ,8281, 4745013, -7.19354363013927, TO_DATE('10/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (11, -90 ,8100, 4641300, -6.95899033584297, TO_DATE('11/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (12, -89 ,7921, 4538733, -6.72959184920286, TO_DATE('12/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (13, -88 ,7744, 4437312, -6.5052908945783, TO_DATE('13/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (14, -87 ,7569, 4337037, -6.28603019632868, TO_DATE('14/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (15, -86 ,7396, 4237908, -6.07175247881336, TO_DATE('15/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (16, -85 ,7225, 4139925, -5.86240046639172, TO_DATE('16/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (17, -84 ,7056, 4043088, -5.65791688342315, TO_DATE('17/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (18, -83 ,6889, 3947397, -5.458244454267, TO_DATE('18/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (19, -82 ,6724, 3852852, -5.26332590328267, TO_DATE('19/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (20, -81 ,6561, 3759453, -5.07310395482953, TO_DATE('20/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (21, -80 ,6400, 3667200, -4.88752133326694, TO_DATE('21/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (22, -79 ,6241, 3576093, -4.7065207629543, TO_DATE('22/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (23, -78 ,6084, 3486132, -4.53004496825097, TO_DATE('23/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (24, -77 ,5929, 3397317, -4.35803667351632, TO_DATE('24/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (25, -76 ,5776, 3309648, -4.19043860310975, TO_DATE('25/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (26, -75 ,5625, 3223125, -4.02719348139061, TO_DATE('26/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (27, -74 ,5476, 3137748, -3.86824403271829, TO_DATE('27/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (28, -73 ,5329, 3053517, -3.71353298145216, TO_DATE('28/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (29, -72 ,5184, 2970432, -3.5630030519516, TO_DATE('29/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (30, -71 ,5041, 2888493, -3.41659696857599, TO_DATE('30/09/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (31, -70 ,4900, 2807700, -3.27425745568469, TO_DATE('01/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (32, -69 ,4761, 2728053, -3.13592723763709, TO_DATE('02/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (33, -68 ,4624, 2649552, -3.00154903879256, TO_DATE('03/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (34, -67 ,4489, 2572197, -2.87106558351048, TO_DATE('04/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (35, -66 ,4356, 2495988, -2.74441959615022, TO_DATE('05/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (36, -65 ,4225, 2420925, -2.62155380107116, TO_DATE('06/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (37, -64 ,4096, 2347008, -2.50241092263268, TO_DATE('07/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (38, -63 ,3969, 2274237, -2.38693368519414, TO_DATE('08/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (39, -62 ,3844, 2202612, -2.27506481311493, TO_DATE('09/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (40, -61 ,3721, 2132133, -2.16674703075442, TO_DATE('10/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (41, -60 ,3600, 2062800, -2.06192306247199, TO_DATE('11/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (42, -59 ,3481, 1994613, -1.96053563262702, TO_DATE('12/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (43, -58 ,3364, 1927572, -1.86252746557887, TO_DATE('13/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (44, -57 ,3249, 1861677, -1.76784128568692, TO_DATE('14/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (45, -56 ,3136, 1796928, -1.67641981731056, TO_DATE('15/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (46, -55 ,3025, 1733325, -1.58820578480916, TO_DATE('16/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (47, -54 ,2916, 1670868, -1.50314191254208, TO_DATE('17/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (48, -53 ,2809, 1609557, -1.42117092486872, TO_DATE('18/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (49, -52 ,2704, 1549392, -1.34223554614843, TO_DATE('19/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (50, -51 ,2601, 1490373, -1.26627850074061, TO_DATE('20/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (51, -50 ,2500, 1432500, -1.19324251300463, TO_DATE('21/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (52, -49 ,2401, 1375773, -1.12307030729985, TO_DATE('22/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (53, -48 ,2304, 1320192, -1.05570460798566, TO_DATE('23/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (54, -47 ,2209, 1265757, -0.991088139421434, TO_DATE('24/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (55, -46 ,2116, 1212468, -0.929163625966546, TO_DATE('25/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (56, -45 ,2025, 1160325, -0.869873791980372, TO_DATE('26/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (57, -44 ,1936, 1109328, -0.813161361822288, TO_DATE('27/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (58, -43 ,1849, 1059477, -0.75896905985167, TO_DATE('28/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (59, -42 ,1764, 1010772, -0.707239610427893, TO_DATE('29/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (60, -41 ,1681, 963213, -0.657915737910334, TO_DATE('30/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (61, -40 ,1600, 916800, -0.610940166658368, TO_DATE('31/10/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (62, -39 ,1521, 871533, -0.566255621031371, TO_DATE('01/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (63, -38 ,1444, 827412, -0.523804825388718, TO_DATE('02/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (64, -37 ,1369, 784437, -0.483530504089786, TO_DATE('03/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (65, -36 ,1296, 742608, -0.44537538149395, TO_DATE('04/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (66, -35 ,1225, 701925, -0.409282181960586, TO_DATE('05/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (67, -34 ,1156, 662388, -0.37519362984907, TO_DATE('06/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (68, -33 ,1089, 623997, -0.343052449518778, TO_DATE('07/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (69, -32 ,1024, 586752, -0.312801365329084, TO_DATE('08/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (70, -31 ,961, 550653, -0.284383101639366, TO_DATE('09/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (71, -30 ,900, 515700, -0.257740382808999, TO_DATE('10/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (72, -29 ,841, 481893, -0.232815933197358, TO_DATE('11/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (73, -28 ,784, 449232, -0.20955247716382, TO_DATE('12/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (74, -27 ,729, 417717, -0.18789273906776, TO_DATE('13/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (75, -26 ,676, 387348, -0.167779443268554, TO_DATE('14/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (76, -25 ,625, 358125, -0.149155314125578, TO_DATE('15/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (77, -24 ,576, 330048, -0.131963075998208, TO_DATE('16/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (78, -23 ,529, 303117, -0.116145453245818, TO_DATE('17/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (79, -22 ,484, 277332, -0.101645170227786, TO_DATE('18/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (80, -21 ,441, 252693, -0.0884049513034867, TO_DATE('19/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (81, -20 ,400, 229200, -0.076367520832296, TO_DATE('20/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (82, -19 ,361, 206853, -0.0654756031735898, TO_DATE('21/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (83, -18 ,324, 185652, -0.0556719226867438, TO_DATE('22/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (84, -17 ,289, 165597, -0.0468992037311338, TO_DATE('23/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (85, -16 ,256, 146688, -0.0391001706661356, TO_DATE('24/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (86, -15 ,225, 128925, -0.0322175478511249, TO_DATE('25/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (87, -14 ,196, 112308, -0.0261940596454775, TO_DATE('26/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (88, -13 ,169, 96837, -0.0209724304085693, TO_DATE('27/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (89, -12 ,144, 82512, -0.0164953844997759, TO_DATE('28/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (90, -11 ,121, 69333, -0.0127056462784732, TO_DATE('29/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (91, -10 ,100, 57300, -0.009545940104037, TO_DATE('30/11/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (92, -9 ,81, 46413, -0.00695899033584297, TO_DATE('01/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (93, -8 ,64, 36672, -0.00488752133326694, TO_DATE('02/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (94, -7 ,49, 28077, -0.00327425745568469, TO_DATE('03/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (95, -6 ,36, 20628, -0.00206192306247199, TO_DATE('04/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (96, -5 ,25, 14325, -0.00119324251300463, TO_DATE('05/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (97, -4 ,16, 9168, -0.000610940166658368, TO_DATE('06/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (98, -3 ,9, 5157, -0.000257740382808999, TO_DATE('07/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (99, -2 ,4, 2292, -0.000076367520832296, TO_DATE('08/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (100, -1 ,1, 573, -0.000009545940104037, TO_DATE('09/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (101, 0 ,0, 0, 0, TO_DATE('10/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (102, 1 ,1, 573, 0.000009545940104037, TO_DATE('11/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (103, 2 ,4, 2292, 0.000076367520832296, TO_DATE('12/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (104, 3 ,9, 5157, 0.000257740382808999, TO_DATE('13/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (105, 4 ,16, 9168, 0.000610940166658368, TO_DATE('14/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (106, 5 ,25, 14325, 0.00119324251300463, TO_DATE('15/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (107, 6 ,36, 20628, 0.00206192306247199, TO_DATE('16/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (108, 7 ,49, 28077, 0.00327425745568469, TO_DATE('17/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (109, 8 ,64, 36672, 0.00488752133326694, TO_DATE('18/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (110, 9 ,81, 46413, 0.00695899033584297, TO_DATE('19/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (111, 10 ,100, 57300, 0.009545940104037, TO_DATE('20/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (112, 11 ,121, 69333, 0.0127056462784732, TO_DATE('21/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (113, 12 ,144, 82512, 0.0164953844997759, TO_DATE('22/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (114, 13 ,169, 96837, 0.0209724304085693, TO_DATE('23/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (115, 14 ,196, 112308, 0.0261940596454775, TO_DATE('24/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (116, 15 ,225, 128925, 0.0322175478511249, TO_DATE('25/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (117, 16 ,256, 146688, 0.0391001706661356, TO_DATE('26/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (118, 17 ,289, 165597, 0.0468992037311338, TO_DATE('27/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (119, 18 ,324, 185652, 0.0556719226867438, TO_DATE('28/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (120, 19 ,361, 206853, 0.0654756031735898, TO_DATE('29/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (121, 20 ,400, 229200, 0.076367520832296, TO_DATE('30/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (122, 21 ,441, 252693, 0.0884049513034867, TO_DATE('31/12/2020', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (123, 22 ,484, 277332, 0.101645170227786, TO_DATE('01/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (124, 23 ,529, 303117, 0.116145453245818, TO_DATE('02/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (125, 24 ,576, 330048, 0.131963075998208, TO_DATE('03/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (126, 25 ,625, 358125, 0.149155314125578, TO_DATE('04/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (127, 26 ,676, 387348, 0.167779443268554, TO_DATE('05/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (128, 27 ,729, 417717, 0.18789273906776, TO_DATE('06/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (129, 28 ,784, 449232, 0.20955247716382, TO_DATE('07/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (130, 29 ,841, 481893, 0.232815933197358, TO_DATE('08/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (131, 30 ,900, 515700, 0.257740382808999, TO_DATE('09/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (132, 31 ,961, 550653, 0.284383101639366, TO_DATE('10/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (133, 32 ,1024, 586752, 0.312801365329084, TO_DATE('11/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (134, 33 ,1089, 623997, 0.343052449518778, TO_DATE('12/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (135, 34 ,1156, 662388, 0.37519362984907, TO_DATE('13/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (136, 35 ,1225, 701925, 0.409282181960586, TO_DATE('14/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (137, 36 ,1296, 742608, 0.44537538149395, TO_DATE('15/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (138, 37 ,1369, 784437, 0.483530504089786, TO_DATE('16/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (139, 38 ,1444, 827412, 0.523804825388718, TO_DATE('17/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (140, 39 ,1521, 871533, 0.566255621031371, TO_DATE('18/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (141, 40 ,1600, 916800, 0.610940166658368, TO_DATE('19/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (142, 41 ,1681, 963213, 0.657915737910334, TO_DATE('20/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (143, 42 ,1764, 1010772, 0.707239610427893, TO_DATE('21/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (144, 43 ,1849, 1059477, 0.75896905985167, TO_DATE('22/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (145, 44 ,1936, 1109328, 0.813161361822288, TO_DATE('23/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (146, 45 ,2025, 1160325, 0.869873791980372, TO_DATE('24/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (147, 46 ,2116, 1212468, 0.929163625966546, TO_DATE('25/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (148, 47 ,2209, 1265757, 0.991088139421434, TO_DATE('26/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (149, 48 ,2304, 1320192, 1.05570460798566, TO_DATE('27/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (150, 49 ,2401, 1375773, 1.12307030729985, TO_DATE('28/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (151, 50 ,2500, 1432500, 1.19324251300463, TO_DATE('29/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (152, 51 ,2601, 1490373, 1.26627850074061, TO_DATE('30/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (153, 52 ,2704, 1549392, 1.34223554614843, TO_DATE('31/01/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (154, 53 ,2809, 1609557, 1.42117092486872, TO_DATE('01/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (155, 54 ,2916, 1670868, 1.50314191254208, TO_DATE('02/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (156, 55 ,3025, 1733325, 1.58820578480916, TO_DATE('03/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (157, 56 ,3136, 1796928, 1.67641981731056, TO_DATE('04/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (158, 57 ,3249, 1861677, 1.76784128568692, TO_DATE('05/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (159, 58 ,3364, 1927572, 1.86252746557887, TO_DATE('06/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (160, 59 ,3481, 1994613, 1.96053563262702, TO_DATE('07/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (161, 60 ,3600, 2062800, 2.06192306247199, TO_DATE('08/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (162, 61 ,3721, 2132133, 2.16674703075442, TO_DATE('09/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (163, 62 ,3844, 2202612, 2.27506481311493, TO_DATE('10/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (164, 63 ,3969, 2274237, 2.38693368519414, TO_DATE('11/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (165, 64 ,4096, 2347008, 2.50241092263268, TO_DATE('12/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (166, 65 ,4225, 2420925, 2.62155380107116, TO_DATE('13/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (167, 66 ,4356, 2495988, 2.74441959615022, TO_DATE('14/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (168, 67 ,4489, 2572197, 2.87106558351048, TO_DATE('15/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (169, 68 ,4624, 2649552, 3.00154903879256, TO_DATE('16/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (170, 69 ,4761, 2728053, 3.13592723763709, TO_DATE('17/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (171, 70 ,4900, 2807700, 3.27425745568469, TO_DATE('18/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (172, 71 ,5041, 2888493, 3.41659696857599, TO_DATE('19/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (173, 72 ,5184, 2970432, 3.5630030519516, TO_DATE('20/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (174, 73 ,5329, 3053517, 3.71353298145216, TO_DATE('21/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (175, 74 ,5476, 3137748, 3.86824403271829, TO_DATE('22/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (176, 75 ,5625, 3223125, 4.02719348139061, TO_DATE('23/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (177, 76 ,5776, 3309648, 4.19043860310975, TO_DATE('24/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (178, 77 ,5929, 3397317, 4.35803667351632, TO_DATE('25/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (179, 78 ,6084, 3486132, 4.53004496825097, TO_DATE('26/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (180, 79 ,6241, 3576093, 4.7065207629543, TO_DATE('27/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (181, 80 ,6400, 3667200, 4.88752133326694, TO_DATE('28/02/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (182, 81 ,6561, 3759453, 5.07310395482953, TO_DATE('01/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (183, 82 ,6724, 3852852, 5.26332590328267, TO_DATE('02/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (184, 83 ,6889, 3947397, 5.458244454267, TO_DATE('03/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (185, 84 ,7056, 4043088, 5.65791688342315, TO_DATE('04/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (186, 85 ,7225, 4139925, 5.86240046639172, TO_DATE('05/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (187, 86 ,7396, 4237908, 6.07175247881336, TO_DATE('06/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (188, 87 ,7569, 4337037, 6.28603019632868, TO_DATE('07/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (189, 88 ,7744, 4437312, 6.5052908945783, TO_DATE('08/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (190, 89 ,7921, 4538733, 6.72959184920286, TO_DATE('09/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (191, 90 ,8100, 4641300, 6.95899033584297, TO_DATE('10/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (192, 91 ,8281, 4745013, 7.19354363013927, TO_DATE('11/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (193, 92 ,8464, 4849872, 7.43330900773236, TO_DATE('12/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (194, 93 ,8649, 4955877, 7.67834374426289, TO_DATE('13/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (195, 94 ,8836, 5063028, 7.92870511537147, TO_DATE('14/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (196, 95 ,9025, 5171325, 8.18445039669872, TO_DATE('15/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (197, 96 ,9216, 5280768, 8.44563686388528, TO_DATE('16/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (198, 97 ,9409, 5391357, 8.71232179257176, TO_DATE('17/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (199, 98 ,9604, 5503092, 8.98456245839879, TO_DATE('18/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (200, 99 ,9801, 5615973, 9.262416137007, TO_DATE('19/03/2021', 'DD/MM/YYYY'));
INSERT INTO sample(id, sample_smallint, sample_int, sample_decimal, sample_numeric, sample_date) VALUES (201, 100 ,10000, 5730000, 9.545940104037, TO_DATE('20/03/2021', 'DD/MM/YYYY'));
