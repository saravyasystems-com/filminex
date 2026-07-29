package com.saravyasystems.filminex.transparency.api;

/** Public boundary for MTI applicability and export-disclosure decisions. */
public interface MediaTransparencyService {

    TransparencyDecision evaluate(TransparencyRequest request);

    ExportDisclosure disclosureFor(TransparencyRequest request);
}
