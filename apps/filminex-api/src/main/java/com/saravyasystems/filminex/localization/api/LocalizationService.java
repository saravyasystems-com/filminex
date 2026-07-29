package com.saravyasystems.filminex.localization.api;

import java.util.List;
import java.util.UUID;

/** Public boundary for subtitle, caption, transcript, and dubbing data. */
public interface LocalizationService {

    LocalizationTrack createTrack(CreateLocalizationTrack request);

    VoiceProfile registerVoiceProfile(RegisterVoiceProfile request);

    LocalizationCue addCue(AddLocalizationCue request);

    LocalizationTrack changeStatus(
            UUID workspaceId, UUID actorUserId, UUID trackId, LocalizationStatus status);

    List<LocalizationTrack> listTracks(UUID workspaceId, UUID sourceId);

    List<LocalizationCue> listCues(UUID workspaceId, UUID trackId);

    List<LocalizationSearchEntry> searchableEntries(UUID workspaceId, UUID sourceId);
}
