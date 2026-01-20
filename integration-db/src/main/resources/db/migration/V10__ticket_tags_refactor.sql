drop table sourceforge_ticket_tags;

create table sourceforge_ticket_tags
(
    internal_id varchar(25) not null references sourceforge_ticket_details (internal_id),
    tag_id      int         not null references ticket_tags (tag_id),

    constraint pk_sf_tt_uk primary key (internal_id, tag_id)
);
