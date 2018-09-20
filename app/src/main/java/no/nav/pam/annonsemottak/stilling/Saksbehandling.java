package no.nav.pam.annonsemottak.stilling;

import io.micrometer.core.instrument.Metrics;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Optional;

@Embeddable
public class Saksbehandling {

    private static final String STATUS_CHANGED_COUNTER = "ad.status.changed";

    private String saksbehandler;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String merknader;

    private String kommentarer;

    protected Saksbehandling() {
        this.status = Status.MOTTATT;
    }

    void oppdaterMed(OppdaterSaksbehandlingCommand command, Stilling stilling)
            throws IllegalSaksbehandlingCommandException {
        valider(command);

        if (command.getStatus().isPresent() && this.status != command.getStatus().get()) {
            Metrics.counter(STATUS_CHANGED_COUNTER + "." + command.getStatus().get().name()).increment();

            this.status = command.getStatus().get();
            if (this.status == Status.GODKJENT)
                stilling.setPublished(LocalDateTime.now());
        }
        if (command.getSaksbehandler().isPresent()) {
            this.saksbehandler = command.getSaksbehandler().get().asString();
        }
        if (command.getMerknader().isPresent()) {
            this.merknader = command.getMerknader().get().asString();
        }
        if (command.getKommentarer().isPresent()) {
            this.kommentarer = command.getKommentarer().get().asString();
        }
    }

    /**
     * Validerer at oppdateringen er gyldig ift. kombinasjon av STATUS og tilhørende felter.
     */
    private void valider(OppdaterSaksbehandlingCommand command)
            throws IllegalSaksbehandlingCommandException {

        if (commandWillChangeAndNotRemoveSaksbehandler(command)) {
            throw new IllegalSaksbehandlingCommandException("Kan bare fjerne saksbehandler dersom en saksbehandler allerede er satt.");
        }

        if (!command.getStatus().isPresent()) {
            //Status må være angitt for at vi skal kunne validere den.
            return;
        }
        Status status = command.getStatus().get();
        if (status.equals(Status.UNDER_ARBEID) && !command.getSaksbehandler().isPresent()) {
            throw new IllegalSaksbehandlingCommandException("Kan ikke sette status til UNDER_ARBEID uten å angi saksbehandler.");
        }
        if (status.equals(Status.GODKJENT) && command.getMerknader().isPresent() && command.getMerknader().get().notEmpty()) {
            throw new IllegalSaksbehandlingCommandException("Kan ikke sette status til GODKJENT når det er knyttet merknader ('" + command.getMerknader().get().asString() + "') til annonsen.");
        }
        if (status.equals(Status.AVVIST) && !command.getMerknader().isPresent()) {
            throw new IllegalSaksbehandlingCommandException("Minst en merknad må være angitt før du kan avvise en annonse.");
        }

    }

    private boolean commandWillChangeAndNotRemoveSaksbehandler(OppdaterSaksbehandlingCommand command) {

        boolean willCauseChange = false;
        if (command.getSaksbehandler().isPresent() && command.getSaksbehandler().get().asString() != null) {

            // Command does not remove Saksbehandler.
            if (getSaksbehandler().isPresent() && !getSaksbehandler().equals(command.getSaksbehandler())) {

                // That Saksbehandler is different from the Saksbehandler in the change command.
                willCauseChange = true;
            }
        }
        return willCauseChange;
    }

    void oppdatert() {
        this.status = Status.OPPDATERT;
    }

    void resetSaksbehandler() {
        this.status = Status.MOTTATT;
        this.saksbehandler = null;
    }

    void rejectAsDuplicate(Integer id) {
        this.status = Status.AVVIST;
        this.merknader = Merknader.Merknad.DUPLIKAT.getKodeAsString();
        this.kommentarer = "Duplikat av id: " + id;
    }

    void rejectBecauseOfCapasity() {
        this.status = Status.AVVIST;
            this.merknader = Merknader.Merknad.IKKE_KAPASITET.getKodeAsString();
            this.kommentarer = "Avvist p.g.a. kapasitet";
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
