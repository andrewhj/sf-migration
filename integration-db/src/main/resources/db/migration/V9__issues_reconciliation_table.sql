CREATE TABLE IF NOT EXISTS SOURCEFORGE_GH_RECONCILED_ISSUES
(
    sf_id varchar(25) not null references sourceforge_ticket_details(internal_id),
    gh_id int not null references github_issues(id),
    constraint SOURCEFORGE_GH_RCNCL_ISSUE_PK
        primary key (sf_id, gh_id)
);
