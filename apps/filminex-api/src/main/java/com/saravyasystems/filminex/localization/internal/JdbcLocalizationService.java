package com.saravyasystems.filminex.localization.internal;

import com.saravyasystems.filminex.localization.api.AddLocalizationCue;
import com.saravyasystems.filminex.localization.api.CreateLocalizationTrack;
import com.saravyasystems.filminex.localization.api.LocaleTag;
import com.saravyasystems.filminex.localization.api.LocalizationCue;
import com.saravyasystems.filminex.localization.api.LocalizationKind;
import com.saravyasystems.filminex.localization.api.LocalizationSearchEntry;
import com.saravyasystems.filminex.localization.api.LocalizationService;
import com.saravyasystems.filminex.localization.api.LocalizationStatus;
import com.saravyasystems.filminex.localization.api.LocalizationTrack;
import com.saravyasystems.filminex.localization.api.RegisterVoiceProfile;
import com.saravyasystems.filminex.localization.api.VoiceOrigin;
import com.saravyasystems.filminex.localization.api.VoiceProfile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

class JdbcLocalizationService implements LocalizationService {

    private final JdbcClient jdbc;

    JdbcLocalizationService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public LocalizationTrack createTrack(CreateLocalizationTrack request) {
        return jdbc.sql("""
                        insert into filminex.localization_track (
                            id, workspace_id, source_id, kind, locale, title, changed_by
                        ) values (
                            :id, :workspaceId, :sourceId, :kind, :locale, :title, :changedBy
                        )
                        returning *
                        """)
                .param("id", request.id())
                .param("workspaceId", request.workspaceId())
                .param("sourceId", request.sourceId())
                .param("kind", request.kind().name())
                .param("locale", request.locale().value())
                .param("title", request.title())
                .param("changedBy", request.actorUserId())
                .query(JdbcLocalizationService::mapTrack)
                .single();
    }

    @Override
    @Transactional
    public VoiceProfile registerVoiceProfile(RegisterVoiceProfile request) {
        return jdbc.sql("""
                        insert into filminex.localization_voice_profile (
                            id, workspace_id, talent_id, label, locale, origin, changed_by
                        ) values (
                            :id, :workspaceId, :talentId, :label, :locale, :origin, :changedBy
                        )
                        returning *
                        """)
                .param("id", request.id())
                .param("workspaceId", request.workspaceId())
                .param("talentId", request.talentId())
                .param("label", request.label())
                .param("locale", request.locale().value())
                .param("origin", request.origin().name())
                .param("changedBy", request.actorUserId())
                .query(JdbcLocalizationService::mapVoiceProfile)
                .single();
    }

    @Override
    @Transactional
    public LocalizationCue addCue(AddLocalizationCue request) {
        requireTrack(request.workspaceId(), request.trackId());
        requireVoiceProfile(request.workspaceId(), request.voiceProfileId());
        return jdbc.sql("""
                        insert into filminex.localization_cue (
                            id, workspace_id, track_id, sequence_number, start_milliseconds,
                            end_milliseconds, text, dialogue_reference, voice_profile_id, changed_by
                        ) values (
                            :id, :workspaceId, :trackId, :sequenceNumber, :startMilliseconds,
                            :endMilliseconds, :text, :dialogueReference, :voiceProfileId, :changedBy
                        )
                        returning *
                        """)
                .param("id", request.id())
                .param("workspaceId", request.workspaceId())
                .param("trackId", request.trackId())
                .param("sequenceNumber", request.sequenceNumber())
                .param("startMilliseconds", request.startMilliseconds())
                .param("endMilliseconds", request.endMilliseconds())
                .param("text", request.text())
                .param("dialogueReference", request.dialogueReference(), Types.VARCHAR)
                .param("voiceProfileId", request.voiceProfileId(), Types.OTHER)
                .param("changedBy", request.actorUserId())
                .query(JdbcLocalizationService::mapCue)
                .single();
    }

    @Override
    @Transactional
    public LocalizationTrack changeStatus(
            UUID workspaceId, UUID actorUserId, UUID trackId, LocalizationStatus status) {
        return jdbc.sql("""
                        update filminex.localization_track
                        set status = :status, changed_by = :changedBy, changed_at = current_timestamp
                        where id = :id and workspace_id = :workspaceId
                        returning *
                        """)
                .param("status", status.name())
                .param("changedBy", actorUserId)
                .param("id", trackId)
                .param("workspaceId", workspaceId)
                .query(JdbcLocalizationService::mapTrack)
                .optional()
                .orElseThrow(() -> new IllegalArgumentException("Localization track not found"));
    }

    @Override
    public List<LocalizationTrack> listTracks(UUID workspaceId, UUID sourceId) {
        return jdbc.sql("""
                        select * from filminex.localization_track
                        where workspace_id = :workspaceId and source_id = :sourceId
                        order by kind, locale
                        """)
                .param("workspaceId", workspaceId)
                .param("sourceId", sourceId)
                .query(JdbcLocalizationService::mapTrack)
                .list();
    }

    @Override
    public List<LocalizationCue> listCues(UUID workspaceId, UUID trackId) {
        requireTrack(workspaceId, trackId);
        return jdbc.sql("""
                        select * from filminex.localization_cue
                        where workspace_id = :workspaceId and track_id = :trackId
                        order by sequence_number
                        """)
                .param("workspaceId", workspaceId)
                .param("trackId", trackId)
                .query(JdbcLocalizationService::mapCue)
                .list();
    }

    @Override
    public List<LocalizationSearchEntry> searchableEntries(UUID workspaceId, UUID sourceId) {
        return jdbc.sql("""
                        select t.id as track_id, c.id as cue_id, t.source_id, t.kind, t.locale,
                               t.title, c.text, c.dialogue_reference
                        from filminex.localization_track t
                        join filminex.localization_cue c on c.track_id = t.id
                        where t.workspace_id = :workspaceId
                          and c.workspace_id = :workspaceId
                          and t.source_id = :sourceId
                          and t.status in ('REVIEWED', 'APPROVED')
                        order by t.id, c.sequence_number
                        """)
                .param("workspaceId", workspaceId)
                .param("sourceId", sourceId)
                .query((resultSet, rowNumber) -> new LocalizationSearchEntry(
                        resultSet.getObject("track_id", UUID.class),
                        resultSet.getObject("cue_id", UUID.class),
                        resultSet.getObject("source_id", UUID.class),
                        LocalizationKind.valueOf(resultSet.getString("kind")),
                        new LocaleTag(resultSet.getString("locale")),
                        resultSet.getString("title"),
                        resultSet.getString("text"),
                        resultSet.getString("dialogue_reference")))
                .list();
    }

    private void requireTrack(UUID workspaceId, UUID trackId) {
        boolean exists = jdbc.sql("""
                        select exists(
                            select 1 from filminex.localization_track
                            where id = :id and workspace_id = :workspaceId
                        )
                        """)
                .param("id", trackId)
                .param("workspaceId", workspaceId)
                .query(Boolean.class)
                .single();
        if (!exists) {
            throw new IllegalArgumentException("Localization track not found");
        }
    }

    private void requireVoiceProfile(UUID workspaceId, UUID voiceProfileId) {
        if (voiceProfileId == null) {
            return;
        }
        boolean exists = jdbc.sql("""
                        select exists(
                            select 1 from filminex.localization_voice_profile
                            where id = :id and workspace_id = :workspaceId
                        )
                        """)
                .param("id", voiceProfileId)
                .param("workspaceId", workspaceId)
                .query(Boolean.class)
                .single();
        if (!exists) {
            throw new IllegalArgumentException("Voice profile not found");
        }
    }

    private static LocalizationTrack mapTrack(ResultSet resultSet, int rowNumber)
            throws SQLException {
        return new LocalizationTrack(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("workspace_id", UUID.class),
                resultSet.getObject("source_id", UUID.class),
                LocalizationKind.valueOf(resultSet.getString("kind")),
                new LocaleTag(resultSet.getString("locale")),
                resultSet.getString("title"),
                LocalizationStatus.valueOf(resultSet.getString("status")),
                resultSet.getObject("changed_by", UUID.class),
                resultSet.getTimestamp("changed_at").toInstant());
    }

    private static LocalizationCue mapCue(ResultSet resultSet, int rowNumber)
            throws SQLException {
        return new LocalizationCue(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("workspace_id", UUID.class),
                resultSet.getObject("track_id", UUID.class),
                resultSet.getInt("sequence_number"),
                resultSet.getLong("start_milliseconds"),
                resultSet.getLong("end_milliseconds"),
                resultSet.getString("text"),
                resultSet.getString("dialogue_reference"),
                resultSet.getObject("voice_profile_id", UUID.class),
                resultSet.getObject("changed_by", UUID.class),
                resultSet.getTimestamp("changed_at").toInstant());
    }

    private static VoiceProfile mapVoiceProfile(ResultSet resultSet, int rowNumber)
            throws SQLException {
        return new VoiceProfile(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("workspace_id", UUID.class),
                resultSet.getObject("talent_id", UUID.class),
                resultSet.getString("label"),
                new LocaleTag(resultSet.getString("locale")),
                VoiceOrigin.valueOf(resultSet.getString("origin")),
                resultSet.getObject("changed_by", UUID.class),
                resultSet.getTimestamp("changed_at").toInstant());
    }
}
