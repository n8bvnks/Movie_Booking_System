-- test accounts: admin / admin123, alice / alice123, sam / sam123
INSERT IGNORE INTO users (username, password, role) VALUES
('admin', 'admin123', 'ADMIN'),
('alice', 'alice123', 'CUSTOMER'),
('sam',   'sam123',   'CUSTOMER');