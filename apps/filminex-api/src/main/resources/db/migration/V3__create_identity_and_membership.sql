create table filminex_user (
    id uuid primary key,
    email varchar(320) not null,
    display_name varchar(160) not null,
    created_at timestamp with time zone not null default current_timestamp,
    updated_at timestamp with time zone not null default current_timestamp,
    constraint filminex_user_email_not_blank check (length(trim(email)) > 0),
    constraint filminex_user_display_name_not_blank check (length(trim(display_name)) > 0)
);

create unique index filminex_user_email_unique_idx on filminex_user (lower(email));

create table workspace_membership (
    workspace_id uuid not null references workspace (id) on delete cascade,
    user_id uuid not null references filminex_user (id),
    role varchar(16) not null,
    joined_at timestamp with time zone not null default current_timestamp,
    primary key (workspace_id, user_id),
    constraint workspace_membership_role_valid check (role in ('VIEWER', 'EDITOR', 'ADMIN'))
);

create index workspace_membership_user_id_idx on workspace_membership (user_id);
