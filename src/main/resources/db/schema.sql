DROP TABLE IF EXISTS users;
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  name VARCHAR(32) NOT NULL,
  password CHAR(64) NOT NULL,
  role VARCHAR(32) NOT NULL
);

DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  owner_id INTEGER NOT NULL,
  price INTEGER NOT NULL,
  description TEXT,
  stock INTEGER NOT NULL
);

DROP TABLE IF EXISTS comments;
CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  category VARCHAR(32) NOT NULL,
  category_id INTEGER NOT NULL,
  owner_id INTEGER NOT NULL,
  create_date TIMESTAMP NOT NULL,
  content TEXT NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE
);

DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  owner_id INTEGER NOT NULL,
  create_date TIMESTAMP NOT NULL,
  status VARCHAR(32) NOT NULL,
  total_price INTEGER NOT NULL,
  cart INTEGER[] NOT NULL
);

DROP TABLE IF EXISTS cart_items;
CREATE TABLE cart_items (
  id SERIAL PRIMARY KEY,
  product_id INTEGER NOT NULL,
  title TEXT NOT NULL,
  price INTEGER NOT NULL,
  description TEXT,
  count INTEGER NOT NULL
);

DROP TABLE IF EXISTS lives;
CREATE TABLE lives (
  id SERIAL PRIMARY KEY,
  title TEXT NOT NULL,
  owner_id INTEGER NOT NULL,
  deleted BOOLEAN NOT NULL DEFAULT FALSE
);
