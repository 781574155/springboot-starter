--liquibase formatted sql

--changeset wuhuaxu:1
CREATE TABLE wechat_user_entity (
    user_id INTEGER PRIMARY KEY,
    open_id VARCHAR(255) NOT NULL UNIQUE,
    nickname VARCHAR(255),
    sex VARCHAR(255),
    province VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    headimgurl VARCHAR(255),
    privilege VARCHAR(255),
    unionid VARCHAR(255),
    create_time TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_entity(id) ON DELETE CASCADE
);
--rollback DROP TABLE wechat_user_entity;