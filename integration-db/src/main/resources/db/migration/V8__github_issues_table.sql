create table github_issues(
    id int not null primary key ,
    number int not null,
    title varchar,
    assignee int null references github_assignees(id)
);