CREATE DATABASE quarkus-social2

CREATE TABLE usuarios(
	id bigserial not null primary key,
	name varchar(100) not null,
	age integer not null
)

SELECT * FROM usuarios