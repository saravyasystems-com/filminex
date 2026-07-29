create table workspace (
    id uuid primary key,
    name varchar(160) not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint workspace_name_not_blank check (length(trim(name)) > 0)
);

create table project (
    id uuid primary key,
    workspace_id uuid not null references workspace (id),
    name varchar(200) not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint project_name_not_blank check (length(trim(name)) > 0),
    constraint project_name_per_workspace unique (workspace_id, name)
);

create index project_workspace_id_idx on project (workspace_id);
