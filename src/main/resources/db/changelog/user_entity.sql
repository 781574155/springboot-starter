--liquibase formatted sql

--changeset wuhuaxu:1
CREATE TABLE user_entity (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    create_time DATETIME NOT NULL
);
--rollback DROP TABLE user_entity;

--changeset wuhuaxu:2
CREATE TABLE user_role_entity (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    role VARCHAR(255) NOT NULL,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user_entity(id) ON DELETE CASCADE,
    CONSTRAINT UK_41f5606aba0976fcda0beb61a83259cf UNIQUE (user_id, role)
);
--rollback DROP TABLE user_role_entity;