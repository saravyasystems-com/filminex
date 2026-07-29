create table workspace_entitlement (
    workspace_id uuid not null references workspace (id) on delete cascade,
    capability varchar(80) not null,
    state varchar(16) not null,
    source varchar(24) not null,
    changed_by uuid not null references filminex_user (id),
    changed_at timestamp with time zone not null default current_timestamp,
    primary key (workspace_id, capability),
    constraint workspace_entitlement_capability_valid
        check (capability in ('AI_STUDIO', 'ANIMATION_STUDIO')),
    constraint workspace_entitlement_state_valid
        check (state in ('ENABLED', 'DISABLED')),
    constraint workspace_entitlement_source_valid
        check (source in ('SUBSCRIPTION', 'ADD_ON', 'WORKSPACE_POLICY'))
);

create index workspace_entitlement_changed_by_idx
    on workspace_entitlement (changed_by);
