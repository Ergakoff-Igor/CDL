create schema if not exists acts_log;

create table acts_log.t_act
(
    id           serial primary key,
    c_month      varchar(8)  not null,
    c_year       smallint    not null,
    c_section    varchar(20) not null,
    c_price      real,
    c_act_status varchar(20)
);