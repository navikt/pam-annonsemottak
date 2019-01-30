package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

import static no.nav.pam.annonsemottak.receivers.Kilde.XML_STILLING;

@Service
class XmlStillingExternalRun {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingExternalRun.class);
    private final ExternalRunService externalRunService;
    private final Supplier<LocalDateTime> lastRunSupplier;

    static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2015, 1, 1, 0, 0, 0);


    @Inject
    public XmlStillingExternalRun(ExternalRunService externalRunService) {

        this.externalRunService = externalRunService;

        lastRunSupplier = () -> latestExternalRun()
                        .map(ExternalRun::getLastRun)
                        .orElse(DEFAULT_DATE);

    }

    private Optional<ExternalRun> latestExternalRun() {
        log.debug("Fetching latest run for xml stilling");
        return Optional.ofNullable(externalRunService.findByNameAndMedium(XML_STILLING.toString(), XML_STILLING.value()));
    }

    Supplier<LocalDateTime> lastRunSupplier() {

        return lastRunSupplier;
    }

    void updateLatest(LocalDateTime lastUpdate) {
        ExternalRun latestRun  = latestExternalRun()
                .map(current -> new ExternalRun(current.getId(), XML_STILLING.toString(), XML_STILLING.value(), lastUpdate))
                .orElseGet(() -> new ExternalRun(XML_STILLING.toString(), XML_STILLING.value(), lastUpdate));

        externalRunService.save(latestRun);
    }
}
