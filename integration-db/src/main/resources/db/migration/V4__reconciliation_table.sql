CREATE TABLE IF NOT EXISTS SOURCEFORGE_GH_RECONCILED
(
    sf_id varchar(30) not null references sourceforge_assignees(id),
    gh_id int not null references github_assignees(id),
    constraint SOURCEFORGE_GH_RECONCILED_PK
        primary key (sf_id, gh_id)
);
