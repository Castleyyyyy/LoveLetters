CREATE TABLE games (
  username varchar(255) NOT NULL UNIQUE,
  hearts int NOT NULL,
  round int NOT NULL,
  PRIMARY KEY (username)
);