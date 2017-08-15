CREATE TABLE LEGOSET ( id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY, NAME VARCHAR(30));
CREATE TABLE MANUAL ( id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1) PRIMARY KEY, MANUAL BIGINT, CONTENT VARCHAR(2000));

ALTER TABLE MANUAL ADD FOREIGN KEY (MANUAL)
REFERENCES LEGOSET(id);
