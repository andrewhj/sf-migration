create table ticket_tags
(
    tag_id int not null primary key,
    tag    varchar(25)
);
create table sourceforge_ticket_tags
(
    ticket_num int not null references sourceforge_ticket_details (internal_id),
    tag_id     int not null references ticket_tags (tag_id),

    constraint pk_sf_tt_uk primary key (ticket_num, tag_id)
);

insert into ticket_tags(tag_id, tag)
values (1, 'bugs'),
       (2, 'feature-requests'),
       (3, 'plugintickets');
