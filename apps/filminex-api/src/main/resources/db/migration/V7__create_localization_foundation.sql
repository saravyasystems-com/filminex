create table filminex.localization_track (
    id uuid primary key,
    workspace_id uuid not null references filminex.workspace(id),
    source_id uuid not null,
    kind text not null,
    locale text not null,
    title text not null,
    status text not null default 'DRAFT',
    changed_by uuid not null references filminex.filminex_user(id),
    changed_at timestamptz not null default current_timestamp,
    constraint localization_track_kind check (kind in ('SUBTITLE', 'CAPTION', 'TRANSCRIPT', 'DUB')),
    constraint localization_track_status check (status in ('DRAFT', 'REVIEWED', 'APPROVED')),
    constraint localization_track_identity unique (workspace_id, source_id, kind, locale)
);

create table filminex.localization_voice_profile (
    id uuid primary key,
    workspace_id uuid not null references filminex.workspace(id),
    talent_id uuid not null,
    label text not null,
    locale text not null,
    origin text not null,
    changed_by uuid not null references filminex.filminex_user(id),
    changed_at timestamptz not null default current_timestamp,
    constraint localization_voice_origin check (origin in ('HUMAN', 'AI', 'HYBRID'))
);

create table filminex.localization_cue (
    id uuid primary key,
    workspace_id uuid not null references filminex.workspace(id),
    track_id uuid not null references filminex.localization_track(id) on delete cascade,
    sequence_number integer not null check (sequence_number > 0),
    start_milliseconds bigint not null check (start_milliseconds >= 0),
    end_milliseconds bigint not null,
    text text not null,
    dialogue_reference text,
    voice_profile_id uuid references filminex.localization_voice_profile(id),
    changed_by uuid not null references filminex.filminex_user(id),
    changed_at timestamptz not null default current_timestamp,
    constraint localization_cue_timing check (end_milliseconds > start_milliseconds),
    constraint localization_cue_sequence unique (track_id, sequence_number)
);

create index localization_track_source
    on filminex.localization_track (workspace_id, source_id);

create index localization_cue_timing
    on filminex.localization_cue (track_id, start_milliseconds, end_milliseconds);
