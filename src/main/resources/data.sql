-- Insertion d'un utilisateur test avec rôle ROLE_USER
INSERT INTO user (id, username, password)
VALUES (9, 'testuser', '$2a$10$nvjOlrUKeAhrLVMahMffwuBmWwPgh58suytbe5S6n.XKR1hk2d3JK'); -- password = test123

-- Insertion du rôle (via table des rôles embarquée avec @ElementCollection)
INSERT INTO user_roles (user_id, roles) VALUES (9, 'ROLE_USER');

-- Insertion d'un utilisateur test avec le rôle ROLE_ADMIN
INSERT INTO user (id, username, password)
VALUES (10, 'testadmin', '$2a$10$nvjOlrUKeAhrLVMahMffwuBmWwPgh58suytbe5S6n.XKR1hk2d3JK'); 

-- Insertion du rôle admin
INSERT INTO user_roles (user_id, roles) VALUES (10, 'ROLE_ADMIN');