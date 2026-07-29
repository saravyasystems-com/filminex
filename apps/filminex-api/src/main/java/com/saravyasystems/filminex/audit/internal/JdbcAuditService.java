package com.saravyasystems.filminex.audit.internal;

import com.saravyasystems.filminex.audit.api.AuditActorType;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditQuery;
import com.saravyasystems.filminex.audit.api.AuditRecord;
import com.saravyasystems.filminex.audit.api.AuditService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

final class JdbcAuditService implements AuditService {

    private static final int MAX_QUERY_LIMIT = 500;
    private static final Set<String> SENSITIVE_KEY_PARTS =
            Set.of("password", "passwd", "secret", "token", "credential", "authorization", "cookie");
    private static final TypeReference<Map<String, String>> DETAIL_TYPE = new TypeReference<>() {};

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    JdbcAuditService(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AuditRecord append(AuditEvent event) {
        validate(event);
        UUID id = UUID.randomUUID();
        Instant occurredAt = event.occurredAt() == null ? Instant.now() : event.occurredAt();
        UUID correlationId =
                event.correlationId() == null ? UUID.randomUUID() : event.correlationId();
        return jdbcClient.sql("""
                        insert into filminex.audit_event (
                            id, workspace_id, actor_type, actor_id, action,
                            subject_type, subject_id, outcome, occurred_at,
                            correlation_id, causation_id, details
                        ) values (
                            :id, :workspaceId, :actorType, :actorId, :action,
                            :subjectType, :subjectId, :outcome, :occurredAt,
                            :correlationId, :causationId, cast(:details as jsonb)
                        )
                        returning *
                        """)
                .param("id", id)
                .param("workspaceId", event.workspaceId())
                .param("actorType", event.actorType().name())
                .param("actorId", normalizeActorId(event))
                .param("action", requireText(event.action(), "action"))
                .param("subjectType", requireText(event.subjectType(), "subjectType"))
                .param("subjectId", requireText(event.subjectId(), "subjectId"))
                .param("outcome", event.outcome().name())
                .param("occurredAt", occurredAt.atOffset(ZoneOffset.UTC))
                .param("correlationId", correlationId)
                .param("causationId", event.causationId())
                .param("details", writeDetails(event.details()))
                .query(this::mapRecord)
                .single();
    }

    @Override
    public Optional<AuditRecord> find(UUID workspaceId, UUID eventId) {
        requireId(workspaceId, "workspaceId");
        requireId(eventId, "eventId");
        return jdbcClient.sql("""
                        select * from filminex.audit_event
                        where workspace_id = :workspaceId and id = :eventId
                        """)
                .param("workspaceId", workspaceId)
                .param("eventId", eventId)
                .query(this::mapRecord)
                .optional();
    }

    @Override
    public List<AuditRecord> query(AuditQuery query) {
        validateQuery(query);
        return jdbcClient.sql("""
                        select * from filminex.audit_event
                        where workspace_id = :workspaceId
                          and (cast(:actorType as varchar) is null or actor_type = :actorType)
                          and (cast(:actorId as varchar) is null or actor_id = :actorId)
                          and (cast(:action as varchar) is null or action = :action)
                          and (cast(:subjectType as varchar) is null or subject_type = :subjectType)
                          and (cast(:subjectId as varchar) is null or subject_id = :subjectId)
                          and (cast(:outcome as varchar) is null or outcome = :outcome)
                          and (cast(:correlationId as uuid) is null
                               or correlation_id = :correlationId)
                          and (cast(:occurredFrom as timestamptz) is null
                               or occurred_at >= :occurredFrom)
                          and (cast(:occurredUntil as timestamptz) is null
                               or occurred_at <= :occurredUntil)
                        order by occurred_at desc, id desc
                        limit :limit
                        """)
                .param("workspaceId", query.workspaceId())
                .param("actorType", enumName(query.actorType()))
                .param("actorId", blankToNull(query.actorId()))
                .param("action", blankToNull(query.action()))
                .param("subjectType", blankToNull(query.subjectType()))
                .param("subjectId", blankToNull(query.subjectId()))
                .param("outcome", enumName(query.outcome()))
                .param("correlationId", query.correlationId())
                .param("occurredFrom", toOffset(query.occurredFrom()))
                .param("occurredUntil", toOffset(query.occurredUntil()))
                .param("limit", query.limit())
                .query(this::mapRecord)
                .list();
    }

    private AuditRecord mapRecord(ResultSet resultSet, int rowNumber) throws SQLException {
        return new AuditRecord(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("workspace_id", UUID.class),
                AuditActorType.valueOf(resultSet.getString("actor_type")),
                resultSet.getString("actor_id"),
                resultSet.getString("action"),
                resultSet.getString("subject_type"),
                resultSet.getString("subject_id"),
                AuditOutcome.valueOf(resultSet.getString("outcome")),
                resultSet.getTimestamp("occurred_at").toInstant(),
                resultSet.getTimestamp("recorded_at").toInstant(),
                resultSet.getObject("correlation_id", UUID.class),
                resultSet.getObject("causation_id", UUID.class),
                readDetails(resultSet.getString("details")));
    }

    private void validate(AuditEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event must not be null");
        }
        requireId(event.workspaceId(), "workspaceId");
        if (event.actorType() == null) {
            throw new IllegalArgumentException("actorType must not be null");
        }
        if (event.outcome() == null) {
            throw new IllegalArgumentException("outcome must not be null");
        }
        requireText(event.action(), "action");
        requireText(event.subjectType(), "subjectType");
        requireText(event.subjectId(), "subjectId");
        event.details().keySet().forEach(this::rejectSensitiveKey);
    }

    private void validateQuery(AuditQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        requireId(query.workspaceId(), "workspaceId");
        if (query.limit() < 1 || query.limit() > MAX_QUERY_LIMIT) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_QUERY_LIMIT);
        }
        if (query.occurredFrom() != null
                && query.occurredUntil() != null
                && query.occurredFrom().isAfter(query.occurredUntil())) {
            throw new IllegalArgumentException("occurredFrom must not be after occurredUntil");
        }
    }

    private String normalizeActorId(AuditEvent event) {
        String actorId = blankToNull(event.actorId());
        if (event.actorType() != AuditActorType.SYSTEM && actorId == null) {
            throw new IllegalArgumentException("actorId is required for USER and AI actors");
        }
        return actorId;
    }

    private void rejectSensitiveKey(String key) {
        String normalized = requireText(key, "detail key")
                .toLowerCase(Locale.ROOT)
                .replace("-", "")
                .replace("_", "");
        if (SENSITIVE_KEY_PARTS.stream().anyMatch(normalized::contains)) {
            throw new IllegalArgumentException("Audit details must not contain sensitive key: " + key);
        }
    }

    private String writeDetails(Map<String, String> details) {
        try {
            return objectMapper.writeValueAsString(details);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException("Audit details could not be serialized", exception);
        }
    }

    private Map<String, String> readDetails(String details) {
        try {
            return objectMapper.readValue(details, DETAIL_TYPE);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Stored audit details could not be read", exception);
        }
    }

    private static String enumName(Enum<?> value) {
        return value == null ? null : value.name();
    }

    private static java.time.OffsetDateTime toOffset(Instant value) {
        return value == null ? null : value.atOffset(ZoneOffset.UTC);
    }

    private static UUID requireId(UUID value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return value;
    }

    private static String requireText(String value, String name) {
        String normalized = blankToNull(value);
        if (normalized == null) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return normalized;
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
