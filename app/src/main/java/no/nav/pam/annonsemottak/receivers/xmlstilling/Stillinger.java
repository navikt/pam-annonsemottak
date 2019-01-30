package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.Stilling;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class Stillinger {

    private final List<Stilling> stillinger;

    Stillinger(List<Stilling> stillinger) {
        this.stillinger = stillinger == null ? Collections.emptyList() : stillinger;
    }

    Optional<LocalDateTime> latestDate() {
        return stillinger.stream()
                .map(Stilling::getSystemModifiedDate)
                .max(LocalDateTime::compareTo);
    }

    List<Stilling> asList() {
        return stillinger;
    }
}
