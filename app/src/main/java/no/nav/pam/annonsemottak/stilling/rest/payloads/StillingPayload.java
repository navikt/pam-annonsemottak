package no.nav.pam.annonsemottak.stilling.rest.payloads;

import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.markdown.MarkdownToHtmlConverter;
import no.nav.pam.annonsemottak.stilling.*;

import java.util.HashMap;
import java.util.Map;


public class StillingPayload {

    private static final String UUID = "uuid";
    private static final String TITTEL = "Tittel";
    private static final String STED = "Sted";
    private static final String ARBEIDSGIVER = "Arbeidsgiver";
    private static final String ARBEIDSGIVERINFORMASJON = "Arbeidsgiverinformasjon";
    private static final String STILLINGSTEKST = "Stillingstekst";
    private static final String MOTTATTDATO = "Mottattdato";
    private static final String SOEKNADSFRIST = "Soeknadsfrist";
    private static final String SAKSBEHANDLER = "saksbehandler";
    private static final String STATUS = "status";
    private static final String MERKNADER = "merknader";
    private static final String KOMMENTARER = "kommentarer";
    private static final String KILDE = "kilde";
    private static final String MEDIUM = "medium";
    private static final String URL = "url";
    private static final String EXTERNALID = "externalid";
    private static final String ANNONSESTATUS = "annonsestatus";
    private static final String EXPIRES = "utlopsdato";

    public static Map<String, String> fromStilling(Stilling stilling) {
        Map<String, String> map = new HashMap<>();
        map.put(UUID, stilling.getUuid());
        map.put(MOTTATTDATO, stilling.getCreated() != null ? stilling.getCreated().toString() : null);
        map.put(TITTEL, stilling.getStillingstittel());
        map.put(STED, stilling.getArbeidssted());
        map.put(ARBEIDSGIVER, stilling.getArbeidsgiver().map(Arbeidsgiver::asString).orElse(null));
        map.put(ARBEIDSGIVERINFORMASJON, MarkdownToHtmlConverter.parse(stilling.getArbeidsgiveromtale()));
        map.put(SOEKNADSFRIST, stilling.getSoeknadsfrist());
        map.put(STILLINGSTEKST, MarkdownToHtmlConverter.parse(stilling.getAnnonsetekst()));
        map.put(SAKSBEHANDLER, stilling.getSaksbehandler().map(Saksbehandler::asString).orElse(null));
        map.put(STATUS, stilling.getStatus().getKodeAsString());
        map.put(MERKNADER, stilling.getMerknader().map(Merknader::asString).orElse(null));
        map.put(KOMMENTARER, stilling.getKommentarer().map(Kommentarer::asString).orElse(null));
        map.put(KILDE, mapKildeToHumanReadable(stilling.getKilde()));
        map.put(MEDIUM, stilling.getMedium());
        map.put(URL, stilling.getUrl());
        map.put(EXTERNALID, stilling.getExternalId());
        map.put(ANNONSESTATUS, stilling.getAnnonseStatus().getCodeAsString());
        map.put(EXPIRES, stilling.getExpires() != null ? stilling.getExpires().toString() : null);
        map.put("hash", stilling.getHash());
        map.putAll(stilling.getProperties());
        return map;
    }

    private static String mapKildeToHumanReadable(String kilde) {
        return kilde.equals(Kilde.DEXI.toString()) ? Kilde.DEXI.value() : kilde;
    }


}