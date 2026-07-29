create table filminex.local_rights_grant (
    id uuid primary key,
    workspace_id uuid not null references filminex.workspace(id),
    talent_id uuid not null,
    uses text[] not null,
    territories text[] not null,
    valid_from timestamptz not null,
    valid_until timestamptz not null,
    evidence_reference text not null,
    revoked boolean not null default false,
    changed_by uuid not null references filminex.filminex_user(id),
    changed_at timestamptz not null default current_timestamp,
    constraint local_rights_grant_valid_window check (valid_until > valid_from)
);

create index local_rights_grant_lookup
    on filminex.local_rights_grant (workspace_id, talent_id, revoked, valid_from, valid_until);
