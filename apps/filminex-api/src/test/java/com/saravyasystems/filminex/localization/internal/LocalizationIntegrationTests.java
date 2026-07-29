package com.saravyasystems.filminex.localization.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import com.saravyasystems.filminex.localization.api.AddLocalizationCue;
import com.saravyasystems.filminex.localization.api.CreateLocalizationTrack;
import com.saravyasystems.filminex.localization.api.LocaleTag;
import com.saravyasystems.filminex.localization.api.LocalizationKind;
import com.saravyasystems.filminex.localization.api.LocalizationService;
import com.saravyasystems.filminex.localization.api.LocalizationStatus;
import com.saravyasystems.filminex.localization.api.LocalizationTrack;
import com.saravyasystems.filminex.localization.api.RegisterVoiceProfile;
import com.saravyasystems.filminex.localization.api.VoiceOrigin;
import com.saravyasystems.filminex.localization.api.VoiceProfile;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h"
})
class LocalizationIntegrationTests {

    @Autowired
    private LocalizationService localization;

    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcClient jdbc;

    @BeforeEach
    @AfterEach
    void prepareDatabase() {
        jdbc.sql("delete from filminex.localization_cue").update();
        jdbc.sql("delete from filminex.localization_voice_profile").update();
        jdbc.sql("delete from filminex.localization_track").update();
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
    void trackAndTimedCuesPreserveLocaleDialogueAndOrdering() {
        Context context = context("subtitles");
        LocalizationTrack track =
                localization.createTrack(track(context, LocalizationKind.SUBTITLE, "ta-IN"));

        localization.addCue(cue(context, track.id(), 2, 2500, 4000, "இரண்டாவது", null));
        localization.addCue(cue(context, track.id(), 1, 0, 2000, "முதலாவது", null));

        assertThat(localization.listCues(context.workspace().id(), track.id()))
                .extracting(cue -> cue.sequenceNumber())
                .containsExactly(1, 2);
        assertThat(track.locale().value()).isEqualTo("ta-IN");
    }

    @Test
    void reviewedAndApprovedTextIsSearchableButDraftTextIsNot() {
        Context context = context("search");
        LocalizationTrack track =
                localization.createTrack(track(context, LocalizationKind.TRANSCRIPT, "en"));
        localization.addCue(cue(context, track.id(), 1, 0, 1000, "searchable dialogue", null));

        assertThat(localization.searchableEntries(context.workspace().id(), context.sourceId()))
                .isEmpty();

        localization.changeStatus(
                context.workspace().id(),
                context.owner().id(),
                track.id(),
                LocalizationStatus.REVIEWED);

        assertThat(localization.searchableEntries(context.workspace().id(), context.sourceId()))
                .singleElement()
                .satisfies(entry -> {
                    assertThat(entry.text()).isEqualTo("searchable dialogue");
                    assertThat(entry.dialogueReference()).isEqualTo("dialogue:1");
                });
    }

    @Test
    void dubCueCanReferenceAProviderNeutralVoiceProfile() {
        Context context = context("dub");
        VoiceProfile voice = localization.registerVoiceProfile(new RegisterVoiceProfile(
                UUID.randomUUID(),
                context.workspace().id(),
                UUID.randomUUID(),
                "Tamil voice",
                new LocaleTag("ta-IN"),
                VoiceOrigin.HYBRID,
                context.owner().id()));
        LocalizationTrack track =
                localization.createTrack(track(context, LocalizationKind.DUB, "ta-IN"));

        assertThat(localization.addCue(
                                cue(context, track.id(), 1, 0, 1200, "வணக்கம்", voice.id()))
                        .voiceProfileId())
                .isEqualTo(voice.id());
    }

    @Test
    void trackAndVoiceReferencesCannotCrossWorkspaceBoundaries() {
        Context first = context("first");
        Context second = context("second");
        LocalizationTrack firstTrack =
                localization.createTrack(track(first, LocalizationKind.CAPTION, "en-IN"));
        VoiceProfile secondVoice = localization.registerVoiceProfile(new RegisterVoiceProfile(
                UUID.randomUUID(),
                second.workspace().id(),
                UUID.randomUUID(),
                "Other workspace voice",
                new LocaleTag("en-IN"),
                VoiceOrigin.HUMAN,
                second.owner().id()));

        assertThat(localization.listTracks(second.workspace().id(), first.sourceId())).isEmpty();
        assertThatThrownBy(() -> localization.addCue(
                        cue(first, firstTrack.id(), 1, 0, 1000, "blocked", secondVoice.id())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Voice profile not found");
        assertThatThrownBy(() -> localization.listCues(second.workspace().id(), firstTrack.id()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Localization track not found");
    }

    @Test
    void localeAndTimingContractsRejectInvalidInput() {
        Context context = context("validation");
        assertThatThrownBy(() -> new LocaleTag("en_US"))
                .isInstanceOf(IllegalArgumentException.class);
        LocalizationTrack track =
                localization.createTrack(track(context, LocalizationKind.SUBTITLE, "en"));
        assertThatThrownBy(
                        () -> cue(context, track.id(), 1, 1000, 1000, "invalid", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private CreateLocalizationTrack track(
            Context context, LocalizationKind kind, String locale) {
        return new CreateLocalizationTrack(
                UUID.randomUUID(),
                context.workspace().id(),
                context.sourceId(),
                kind,
                new LocaleTag(locale),
                kind + " track",
                context.owner().id());
    }

    private AddLocalizationCue cue(
            Context context,
            UUID trackId,
            int sequence,
            long start,
            long end,
            String text,
            UUID voiceProfileId) {
        return new AddLocalizationCue(
                UUID.randomUUID(),
                context.workspace().id(),
                trackId,
                context.owner().id(),
                sequence,
                start,
                end,
                text,
                "dialogue:" + sequence,
                voiceProfileId);
    }

    private Context context(String name) {
        UserIdentity owner = identities.registerUser(
                name + "-" + UUID.randomUUID() + "@filminex.test", name);
        Workspace workspace = identities.createWorkspace(owner.id(), name + " workspace");
        return new Context(owner, workspace, UUID.randomUUID());
    }

    private record Context(UserIdentity owner, Workspace workspace, UUID sourceId) {}
}
