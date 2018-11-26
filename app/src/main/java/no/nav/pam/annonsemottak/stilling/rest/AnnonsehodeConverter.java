package no.nav.pam.annonsemottak.stilling.rest;

import no.nav.pam.annonsemottak.stilling.Arbeidsgiver;
import no.nav.pam.annonsemottak.stilling.Merknader;
import no.nav.pam.annonsemottak.stilling.Saksbehandler;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.rest.payloads.AnnonsehodePayload;

import java.util.function.Function;

class AnnonsehodeConverter implements Function<Stilling, AnnonsehodePayload> {


    @Override
    public AnnonsehodePayload apply(Stilling source) {
        return new AnnonsehodePayload.Builder()
                .setUuid(source.getUuid())
                .setArbeidsgiver(source.getArbeidsgiver().map(Arbeidsgiver::asString).orElse(null))
                .setArbeidssted(source.getArbeidssted())
                .setMerknader(source.getMerknader().map(Merknader::asString).orElse(null))
                .setMottattDato(source.getCreated() == null ? null : source.getCreated().toString()) // TODO: Should never need to check for null, see PAMUTV-180.
                .setSaksbehandler(source.getSaksbehandler().map(Saksbehandler::asString).orElse(null))
                .setStatus(source.getStatus().getKodeAsString())
                .setTittel(source.getStillingstittel())
                .setKilde(source.getKilde())
                .setAnnonsestatus((source.getAnnonseStatus() != null) ? source.getAnnonseStatus().getCodeAsString() : null)
                .setSoknadsfrist(source.getSoeknadsfrist())
                .setKommentarer((source.getKommentarer().isPresent()) ? source.getKommentarer().get().asString() : null)
                .setModifisertDato((source.getUpdated() != null) ? source.getUpdated().toString() : null) // TODO: Should never need to check for null, see PAMUTV-180.
                .build();
    }
}
