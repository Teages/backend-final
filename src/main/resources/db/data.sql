DELETE FROM users;

INSERT INTO users (name, password, role) VALUES
('Teages', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'host'),
-- Teages, password, host
('user', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'user'),
-- user, password, user
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin');
-- admin, admin, admin

INSERT INTO products (title, owner_id, price, description, stock)
SELECT 'Test Product', id, 100, 'Product Description', 10
FROM users
WHERE name = 'Teages';

INSERT INTO comments (category, category_id, owner_id, create_date, content)
SELECT 'product', p.id, u.id, NOW(), 'Test Comment'
FROM products as p, users as u
WHERE p.title = 'Test Product'
  AND u.name = 'Teages';

INSERT INTO comments (category, category_id, owner_id, create_date, content)
SELECT 'comment', 1, u.id, NOW(), 'Test Reply Comment'
FROM users as u
WHERE u.name = 'Teages';
