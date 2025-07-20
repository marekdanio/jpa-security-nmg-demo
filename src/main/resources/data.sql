
-- Wstawienie użytkowników z hashem dla hasła 'e5'
INSERT INTO USERS (username, email, password, role, created_at, updated_at) VALUES
                                                                                ('admin', 'admin@gamelib.com', '$2a$10$UvZikcpSbybXbHLwUqlA/OPCMCc7hd1rpy4MFA.GloQale4WqSgWO', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                ('john', 'john@example.com', '$2a$10$UvZikcpSbybXbHLwUqlA/OPCMCc7hd1rpy4MFA.GloQale4WqSgWO', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                ('jane', 'jane@example.com', '$2a$10$UvZikcpSbybXbHLwUqlA/OPCMCc7hd1rpy4MFA.GloQale4WqSgWO', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Wstawienie gier
INSERT INTO GAMES (title, description, genre, platform, release_date, total_copies, available_copies, created_at, updated_at) VALUES
                                                                                                                                  ('The Legend of Zelda: Breath of the Wild', 'Eksploruj ogromny świat Hyrule w tej przygodowej grze akcji.', 'Adventure', 'Nintendo Switch', '2017-03-03', 5, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                  ('God of War', 'Kratos i Atreus wyruszają w epicką podróż przez nordyckie krainy.', 'Action', 'PlayStation 4', '2018-04-20', 3, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                  ('Cyberpunk 2077', 'Futurystyczne RPG w świecie Night City.', 'RPG', 'PC', '2020-12-10', 4, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                  ('Animal Crossing: New Horizons', 'Zbuduj swoje wymarzone życie na bezludnej wyspie.', 'Simulation', 'Nintendo Switch', '2020-03-20', 6, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                  ('The Last of Us Part II', 'Kontynuacja kultowej przygody Ellie w postapokaliptycznym świecie.', 'Action', 'PlayStation 4', '2020-06-19', 2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Wstawienie wypożyczeń
INSERT INTO GAME_RENTAL (user_id, game_id, rental_date, due_date, return_date, status, created_at, updated_at) VALUES
                                                                                                                   (2, 1, '2024-01-15', '2024-01-29', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                   (3, 2, '2024-01-18', '2024-02-01', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                   (2, 4, '2024-01-20', '2024-02-03', NULL, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                   (2, 3, '2024-01-01', '2024-01-15', '2024-01-14', 'RETURNED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                   (3, 1, '2024-01-05', '2024-01-19', '2024-01-18', 'RETURNED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);