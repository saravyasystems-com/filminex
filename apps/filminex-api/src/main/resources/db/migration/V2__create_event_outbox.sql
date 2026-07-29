create table event_outbox (
    id uuid primary key,
    workspace_id uuid not null references workspace (id),
    aggregate_type varchar(100) not null,
    aggregate_id varchar(200) not null,
    event_type varchar(160) not null,
    payload jsonb not null,
    occurred_at timestamp with time zone not null,
    status varchar(20) not null default 'PENDING',
    attempts integer not null default 0,
    next_attempt_at timestamp with time zone not null default current_timestamp,
    processed_at timestamp with time zone,
    last_error varchar(2000),
    constraint event_outbox_status_valid
        check (status in ('PENDING', 'PROCESSING', 'PROCESSED')),
    constraint event_outbox_attempts_non_negative check (attempts >= 0),
    constraint event_outbox_aggregate_type_not_blank check (length(trim(aggregate_type)) > 0),
    constraint event_outbox_aggregate_id_not_blank check (length(trim(aggregate_id)) > 0),
    constraint event_outbox_event_type_not_blank check (length(trim(event_type)) > 0)
);

create index event_outbox_dispatch_idx
    on event_outbox (status, next_attempt_at, occurred_at);
create index event_outbox_workspace_idx
    on event_outbox (workspace_id, occurred_at);
