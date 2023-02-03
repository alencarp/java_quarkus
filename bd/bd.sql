CREATE DATABASE quarkus-social2

CREATE TABLE usuarios(
	id bigserial not null primary key,
	name varchar(100) not null,
	age integer not null
)

SELECT * FROM usuarios;

CREATE TABLE posts (
	id bigserial not null primary key,
	post_text varchar(150) not null,
    data_hora timestamp not null,
    usuario_id bigint not null references usuarios(id)
);

SELECT * FROM posts;

CREATE TABLE followers (
    id bigserial not null primary key,
    usuario_id bigint not null references usuarios(id),
    follower_id bigint not null references usuarios(id)
);
SELECT * FROM followers;
