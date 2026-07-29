create table audit_event (
    id uuid primary key,
    workspace_id uuid not null references workspace (id),
    actor_type varchar(16) not null,
    actor_id varchar(200),
    action varchar(160) not null,
    subject_type varchar(120) not null,
    subject_id varchar(200) not null,
    outcome varchar(16) not null,
    occurred_at timestamp with time zone not null,
    correlation_id uuid not null,
    causation_id uuid,
    details jsonb not null default '{}'::jsonb,
    recorded_at timestamp with time zone not null default current_timestamp,
    constraint audit_event_actor_type_valid check (actor_type in ('USER', 'AI', 'SYSTEM')),
    constraint audit_event_actor_id_valid check (actor_type = 'SYSTEM' or actor_id is not null),
    constraint audit_event_outcome_valid check (outcome in ('SUCCEEDED', 'DENIED', 'FAILED')),
    constraint audit_event_action_not_blank check (length(trim(action)) > 0),
    constraint audit_event_subject_type_not_blank check (length(trim(subject_type)) > 0),
    constraint audit_event_subject_id_not_blank check (length(trim(subject_id)) > 0),
    constraint audit_event_details_object check (jsonb_typeof(details) = 'object')
);

create index audit_event_workspace_time_idx
    on audit_event (workspace_id, occurred_at desc, id desc);
create index audit_event_workspace_subject_idx
    on audit_event (workspace_id, subject_type, subject_id, occurred_at desc);
create index audit_event_workspace_correlation_idx
    on audit_event (workspace_id, correlation_id);

create function reject_audit_event_mutation()
returns trigger
language plpgsql
as $$
begin
    raise exception 'audit events are append-only';
end;
$$;

create trigger audit_event_append_only
before update or delete on audit_event
for each row execute function reject_audit_event_mutation();
