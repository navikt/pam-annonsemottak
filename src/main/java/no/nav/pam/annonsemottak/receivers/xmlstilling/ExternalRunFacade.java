package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.function.Function;

import static no.nav.pam.annonsemottak.receivers.Kilde.XML_STILLING;

@Service
class ExternalRunFacade {

    private static final Logger log = LoggerFactory.getLogger(ExternalRunFacade.class);
    private final ExternalRunService externalRunService;

    static final LocalDateTime DEFAULT_DATE = LocalDateTime.of(2015, 1, 1, 0, 0, 0);


    @Inject
    ExternalRunFacade(ExternalRunService externalRunService) {

        this.externalRunService = externalRunService;

    }

    Stillinger decorate(Function<LocalDateTime, Stillinger> stillingUpdater) {

        log.debug("Fetching latest run for xml stilling");

        ExternalRun run = externalRunService.findByNameAndMedium(XML_STILLING.toString(), XML_STILLING.value());
        LocalDateTime nextRunStart = run != null ? run.getLastRun() : DEFAULT_DATE;

        log.debug("Next run for xml stilling: {} " + nextRunStart);

        Stillinger stillinger = stillingUpdater.apply(nextRunStart);

        stillinger.latestDate().ifPresent(lastUpdate -> {
            ExternalRun newRun = run != null ?
                    new ExternalRun(run.getId(), XML_STILLING.toString(), XML_STILLING.value(), lastUpdate) :
                    new ExternalRun(XML_STILLING.toString(), XML_STILLING.value(), lastUpdate);

            externalRunService.save(newRun);
        });

        return stillinger;
    }

}
