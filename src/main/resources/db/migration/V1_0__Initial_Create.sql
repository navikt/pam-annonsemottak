CREATE SEQUENCE HIBERNATE_SEQUENCE;

CREATE TABLE STILLING
(
  ID                   NUMERIC(19, 0),
  CREATED              TIMESTAMP(6),
  CREATEDBY            VARCHAR(255),
  UPDATED              TIMESTAMP(6),
  UPDATEDBY            VARCHAR(255),
  CREATEDBYDISPLAYNAME VARCHAR(255),
  UPDATEDBYDISPLAYNAME VARCHAR(255),
  EXTERNALID           VARCHAR(255)        NOT NULL,
  PLACE                VARCHAR(255),
  TITLE                VARCHAR(255),
  DUEDATE              VARCHAR(255),
  EMPLOYER             VARCHAR(255),
  EMPLOYERDESCRIPTION  TEXT,
  JOBDESCRIPTION       TEXT,
  HASH                 VARCHAR(255)        NOT NULL,
  UUID                 CHAR(36) DEFAULT '' NOT NULL,
  STATUS               VARCHAR(36)         NOT NULL,
  SAKSBEHANDLER        VARCHAR(36),
  MERKNADER            VARCHAR(255),
  KOMMENTARER          VARCHAR(512),
  KILDE                VARCHAR(36)         NOT NULL,
  MEDIUM               VARCHAR(36)         NOT NULL,
  URL                  VARCHAR(512),
  ANNONSESTATUS        VARCHAR(36),
  EXPIRES              TIMESTAMP(6),
  PUBLISHED            TIMESTAMP(6),
  CONSTRAINT PK_STILLING_ID PRIMARY KEY (ID),
  CONSTRAINT UQ_STILLING_UUID UNIQUE (UUID),
  CONSTRAINT UQ_KILDE_MEDIUM_EXTERNALID UNIQUE (KILDE, MEDIUM, EXTERNALID)
);

CREATE INDEX IX_KILDE_MEDIUM_ANNONSESTATUS
  ON STILLING (KILDE, MEDIUM, ANNONSESTATUS);

CREATE TABLE STILLING_PROPERTIES
(
  STILLING_ID      NUMERIC(19, 0) NOT NULL,
  PROPERTIES_KEY   VARCHAR(255)   NOT NULL,
  PROPERTIES_VALUE VARCHAR(4000)  NOT NULL,
  CONSTRAINT UQ_STILLING_PROPERTIES_KEY UNIQUE (STILLING_ID, PROPERTIES_KEY)
);

CREATE INDEX IX_STILLING_PROPERTIES
  ON STILLING_PROPERTIES (STILLING_ID);


CREATE TABLE EXTERNALRUN
(
  ID      NUMERIC(19, 0) NOT NULL,
  NAME    VARCHAR(255)   NOT NULL,
  MEDIUM  VARCHAR(255),
  LASTRUN TIMESTAMP,
  UNIQUE (NAME, MEDIUM)
);

CREATE SEQUENCE EXTERNALRUN_SEQUENCE START WITH 1;

/* A table for shedlock */
CREATE TABLE SHEDLOCK (
  NAME       VARCHAR(64),
  LOCK_UNTIL TIMESTAMP(3) NULL,
  LOCKED_AT  TIMESTAMP(3) NULL,
  LOCKED_BY  VARCHAR(255),
  PRIMARY KEY (NAME)
);
