package no.nav.pam.annonsemottak.stilling;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Optional;


/**
 * Denne brukes i praksis ikke lenger.
 * Statuys på saksbehandling oppdateres ikke med data fra pam-ad
 */
@Embeddable
public class Saksbehandling {

    private String saksbehandler;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String merknader;

    private String kommentarer;

    protected Saksbehandling() {
        this.status = Status.MOTTATT;
    }

    public Status getStatus() {
        return status;
    }

    public Optional<Saksbehandler> getSaksbehandler() {
        return Saksbehandler.ofNullable(saksbehandler);
    }

    public Optional<Merknader> getMerknader() {
        return Merknader.ofNullable(merknader);
    }

    public Optional<Kommentarer> getKommentarer() {
        return Kommentarer.ofNullable(kommentarer);
    }

}
