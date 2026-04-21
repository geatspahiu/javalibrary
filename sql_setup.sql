create database libraria;
use libraria;

create table books (
    id int auto_increment primary key,
    title varchar(100) not null,
    author varchar(100) not null,
    category varchar(100),
    quantity int,
    price double,
    isbn varchar(100)
);
