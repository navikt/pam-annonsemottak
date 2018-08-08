package no.nav.pam.annonsemottak.stilling;

import no.nav.pam.annonsemottak.MapOperations;

import java.util.Map;
import java.util.Optional;

import static no.nav.pam.annonsemottak.stilling.Saksbehandler.nullSaksbehandler;

public class OppdaterSaksbehandlingCommand extends MapOperations {

    public static final String SAKSBEHANDLER = "saksbehandler";
    public static final String STATUS = "status";
    public static final String MERKNADER = "merknader";
    public static final String KOMMENTARER = "kommentarer";

    public OppdaterSaksbehandlingCommand(Map<String, String> map) {
        super(map);
    }

    /**
     * Returns Saksbehandler if present. Else Optional.empty().
     */
    Optional<Saksbehandler> getSaksbehandler() {
        if (!contains(SAKSBEHANDLER)) {
            return Optional.empty();
        }

        return get(SAKSBEHANDLER) == null || get(SAKSBEHANDLER).isEmpty() ?
                Optional.of(nullSaksbehandler()) : Optional.of(new Saksbehandler(get(SAKSBEHANDLER)));
    }

    Optional<Status> getStatus() {
        if (!contains(STATUS)) {
            return Optional.empty();
        }
        return Optional.of(Status.valueOfStatuskode(get(STATUS)));
    }

    Optional<Merknader> getMerknader() {
        if (!contains(MERKNADER)) {
            return Optional.empty();
        }
        return get(MERKNADER) == null || get(MERKNADER).isEmpty() ?
                Optional.of(Merknader.nullMerknader()) : Optional.of(new Merknader(get(MERKNADER)));
    }


    Optional<Kommentarer> getKommentarer() {
        if (!contains(KOMMENTARER)) {
            return Optional.empty();
        }
        return get(KOMMENTARER) == null || get(KOMMENTARER).isEmpty() ?
                Optional.of(Kommentarer.nullKommentarer()) : Optional.of(new Kommentarer(get(KOMMENTARER)));
    }
}
