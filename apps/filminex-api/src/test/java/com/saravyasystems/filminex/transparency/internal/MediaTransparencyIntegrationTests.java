package com.saravyasystems.filminex.transparency.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import com.saravyasystems.filminex.transparency.api.DisclosureMethod;
import com.saravyasystems.filminex.transparency.api.ExportDisclosure;
import com.saravyasystems.filminex.transparency.api.MediaKind;
import com.saravyasystems.filminex.transparency.api.MediaTransparencyService;
import com.saravyasystems.filminex.transparency.api.TransparencyDecision;
import com.saravyasystems.filminex.transparency.api.TransparencyProductionMode;
import com.saravyasystems.filminex.transparency.api.TransparencyReason;
import com.saravyasystems.filminex.transparency.api.TransparencyRequest;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h"
})
class MediaTransparencyIntegrationTests {

    @Autowired
    private MediaTransparencyService transparency;

    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcClient jdbc;

    @BeforeEach
    void prepareDatabase() {
        jdbc.sql("delete from filminex.localization_cue").update();
        jdbc.sql("delete from filminex.localization_track").update();
        jdbc.sql("delete from filminex.localization_voice_profile").update();
        jdbc.sql("delete from filminex.local_rights_grant").update();
        jdbc.sql("truncate table filminex.audit_event").update();
        jdbc.sql("delete from filminex.workspace_entitlement").update();
        jdbc.sql("delete from filminex.event_outbox").update();
        jdbc.sql("delete from filminex.project").update();
        jdbc.sql("delete from filminex.workspace_membership").update();
        jdbc.sql("delete from filminex.workspace").update();
        jdbc.sql("delete from filminex.filminex_user").update();
    }

    @Test
    void aiAndHybridSyntheticHumanMediaRequireMti001() {
        Context context = context("applicable");
        TransparencyDecision image = transparency.evaluate(request(
                context, MediaKind.IMAGE, TransparencyProductionMode.AI, true, false, true));
        ExportDisclosure video = transparency.disclosureFor(request(
                context, MediaKind.VIDEO, TransparencyProductionMode.HYBRID, true, false, true));

        assertThat(image.required()).isTrue();
        assertThat(image.reason())
                .isEqualTo(TransparencyReason.APPLICABLE_SYNTHETIC_HUMAN_MEDIA);
        assertThat(video.method()).isEqualTo(DisclosureMethod.ICON);
        assertThat(video.categoryId()).contains("MTI-001");
    }

    @Test
    void applicableAudioUsesMetadataInsteadOfAnIcon() {
        Context context = context("audio");
        ExportDisclosure disclosure = transparency.disclosureFor(request(
                context, MediaKind.AUDIO, TransparencyProductionMode.AI, false, true, true));

        assertThat(disclosure.method()).isEqualTo(DisclosureMethod.METADATA);
        assertThat(disclosure.categoryId()).contains("MTI-001");
    }

    @Test
    void nonAiOrNonHumanMediaDoesNotRequireDisclosure() {
        Context context = context("excluded");
        assertThat(transparency
                        .evaluate(request(
                                context,
                                MediaKind.VIDEO,
                                TransparencyProductionMode.REAL,
                                true,
                                true,
                                true))
                        .reason())
                .isEqualTo(TransparencyReason.NON_AI_PRODUCTION);
        assertThat(transparency
                        .evaluate(request(
                                context,
                                MediaKind.IMAGE,
                                TransparencyProductionMode.AI,
                                false,
                                false,
                                true))
                        .reason())
                .isEqualTo(TransparencyReason.NO_REAL_HUMAN_LIKENESS_OR_VOICE);
    }

    @Test
    void unalteredHumanMediaDoesNotRequireDisclosure() {
        Context context = context("unaltered");
        ExportDisclosure disclosure = transparency.disclosureFor(request(
                context, MediaKind.VIDEO, TransparencyProductionMode.HYBRID, true, false, false));

        assertThat(disclosure.decision().reason())
                .isEqualTo(TransparencyReason.NOT_SYNTHETIC_OR_MATERIALLY_ALTERED);
        assertThat(disclosure.method()).isEqualTo(DisclosureMethod.NONE);
        assertThat(disclosure.categoryId()).isEmpty();
    }

    @Test
    void decisionsAreAuditedInsideTheirWorkspace() {
        Context first = context("first");
        Context second = context("second");
        transparency.evaluate(request(
                first, MediaKind.IMAGE, TransparencyProductionMode.AI, true, false, true));
        transparency.evaluate(request(
                second, MediaKind.AUDIO, TransparencyProductionMode.REAL, false, true, false));

        assertThat(jdbc.sql("""
                                select count(*) from filminex.audit_event
                                where workspace_id = :workspaceId
                                  and action = 'transparency.mti-evaluated'
                                """)
                        .param("workspaceId", first.workspace().id())
                        .query(Long.class)
                        .single())
                .isEqualTo(1);
        assertThat(jdbc.sql("""
                                select details ->> 'category'
                                from filminex.audit_event
                                where workspace_id = :workspaceId
                                """)
                        .param("workspaceId", first.workspace().id())
                        .query(String.class)
                        .single())
                .isEqualTo("MTI-001");
    }

    private TransparencyRequest request(
            Context context,
            MediaKind kind,
            TransparencyProductionMode mode,
            boolean likeness,
            boolean voice,
            boolean altered) {
        UUID id = UUID.randomUUID();
        return new TransparencyRequest(
                id,
                context.workspace().id(),
                context.owner().id().toString(),
                "asset://" + UUID.randomUUID(),
                kind,
                mode,
                likeness,
                voice,
                altered,
                List.of("provenance://" + id),
                id);
    }

    private Context context(String name) {
        UserIdentity owner = identities.registerUser(
                name + "-" + UUID.randomUUID() + "@filminex.test", name);
        return new Context(owner, identities.createWorkspace(owner.id(), name + " workspace"));
    }

    private record Context(UserIdentity owner, Workspace workspace) {}
}
