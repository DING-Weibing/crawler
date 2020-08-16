create table IF NOT EXISTS LINKS_TO_BE_PROCESSED
(
    LINK VARCHAR(2000)
);
create table IF NOT EXISTS LINKS_ALREADY_PROCESSED
(
    LINK VARCHAR(2000)
);
create table IF NOT EXISTS NEWS
(
    ID          BIGINT auto_increment,
    TITLE       TEXT,
    CONTENT     TEXT,
    URL         VARCHAR(2000),
    CREATED_AT  TIMESTAMP default NOW(),
    MODIFIED_AT TIMESTAMP default NOW(),
    constraint NEWS_PK
        primary key (ID)
);
