create table sourceforge_ticket_details(
    internal_id varchar(25) not null primary key,
    ticket_num int not null,
    created_date varchar(50),
    assigned_to varchar(50),
    assigned_to_id varchar(50),
    summary varchar(256),
    status varchar(25) not null,
    description text
);