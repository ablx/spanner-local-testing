CREATE INDEX UserByName on users(name);
ALTER INDEX UserByName ADD STORED COLUMN email;
