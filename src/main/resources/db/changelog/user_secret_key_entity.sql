--liquibase formatted sql

--changeset wuhuaxu:1
CREATE TABLE user_secret_key_entity (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(255),
    secret_key VARCHAR(255) NOT NULL UNIQUE,
    create_time DATETIME NOT NULL,
    last_use_time DATETIME,
    default_key BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_entity(id) ON DELETE CASCADE
);
--rollback DROP TABLE user_secret_key_entity;