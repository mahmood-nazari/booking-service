SET
time_zone = '+00:00';

CREATE TABLE available_slots
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_time  DATETIME NOT NULL,
    end_time    DATETIME NOT NULL,
    is_reserved BOOLEAN           DEFAULT FALSE,
    version     BIGINT   NOT NULL DEFAULT 0
);


INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 09:00:00', '2024-12-29 10:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 10:00:00', '2024-12-29 11:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 11:00:00', '2024-12-29 12:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 12:00:00', '2024-12-29 13:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 13:00:00', '2024-12-29 14:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 14:00:00', '2024-12-29 15:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 15:00:00', '2024-12-29 16:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-29 16:00:00', '2024-12-29 17:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-30 09:00:00', '2024-12-30 10:00:00', FALSE);
INSERT INTO available_slots (start_time, end_time, is_reserved)
VALUES ('2024-12-30 10:00:00', '2024-12-30 11:00:00', FALSE);


CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(255) NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO users (username, email, password)
VALUES ('user1',
        'johndoe@example.com', '$2a$12$Qnzfwz8.Mn359cPiu8lssOuLyYnI9C769IKHk4kZ/JMF0lHiwp5hW');
INSERT INTO users (username, email, password)
VALUES ('user2', 'janedoe@example.com', '$2a$12$yJoqVVi89AeZFq6U1wr7ouYZxNjF7LI2J6u83Cr9/ncCtXCi7u.fO');
INSERT INTO users (username, email, password)
VALUES ('user3',
        'user123@example.com', '$2a$12$FZUQ6ASVOwub2HmoShGhbecxWjhb83fIU.itEGY6azrOEMiuLuoq2');



CREATE TABLE reservations
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_id      BIGINT       NOT NULL,
    user_id      BIGINT       NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMP(6) NOT NULL,
    cancelled_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_reservation_slot FOREIGN KEY (slot_id) REFERENCES available_slots (id),
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users (id)
);