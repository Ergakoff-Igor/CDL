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
insert into acts_log.t_act (id, c_month, c_year, c_section, c_price, c_act_status)
values (3, 'Январь', 2024, 'ЭМ', 2000, 'ACCEPTED'),
       (2, 'Февраль', 2024, 'АК', 1000, 'CHECKING_QC'),
       (1, 'Февраль', 2024, 'ЭМ', 3000, 'CHECKING_PTD'),
       (4, 'Март', 2024, 'ТХ', 4000, 'CHECKING_QC');