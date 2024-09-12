--liquibase formatted sql

--changeset wuhuaxu:1
CREATE TABLE system_config_entity (
    name VARCHAR(255) NOT NULL PRIMARY KEY,
    enable_page_register BOOLEAN NOT NULL,
    enable_page_login BOOLEAN NOT NULL,
    enable_wechat_register BOOLEAN NOT NULL,
    enable_wechat_login BOOLEAN NOT NULL,
    enable_wechat_share BOOLEAN NOT NULL,
    wechat_app_id VARCHAR(255),
    wechat_app_secret VARCHAR(255)
);
--rollback DROP TABLE IF EXISTS system_config_entity;