package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.*;

@Service
public class XmlStillingService {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingService.class);
    private final XmlStillingConnector xmlStillinger;
    private final XmlStillingExternalRun externalRuns;
    private final StillingRepository stillingRepository;
    private final XmlStillingMetrics metrics;


    @Inject
    public XmlStillingService(
            final XmlStillingConnector connector,
            XmlStillingExternalRun externalRuns,
            StillingRepository stillingRepository,
            XmlStillingMetrics metrics) {

        this.xmlStillinger = connector;
        this.externalRuns = externalRuns;
        this.stillingRepository = stillingRepository;
        this.metrics = metrics;
    }

    public List<Stilling> updateLatest(boolean saveAllFetchedAds) {

        Function<LocalDateTime, Stillinger> saveMethod = saveAllFetchedAds ? this::saveAllStillinger : this::saveOnlyNewAndChangedStillinger;

        Stillinger stillinger = externalRuns.decorate(saveMethod);

        metrics.registerFor(stillinger);

        return stillinger.asList();

    }

    private Stillinger saveAllStillinger(LocalDateTime nextRunStart) {
        return saveStillinger(nextRunStart, this::saveAllGroupingStrategy);
    }

    private Stillinger saveOnlyNewAndChangedStillinger(LocalDateTime nextRunStart) {
        return saveStillinger(nextRunStart, this::saveOnlyNewAndChangedGroupingStrategy);
    }








    private Stillinger saveStillinger(LocalDateTime nextRunStart, Function<Stilling, Gruppe> grupperingStrategi) {

        Stillinger stillinger = new Stillinger(xmlStillinger.fetchFrom(nextRunStart), grupperingStrategi);

        log.debug("Siste stilling mottat {} fra xml-stilling", stillinger.latestDate());

        stillinger.get(CHANGED).ifPresent(list -> list.forEach(this::mergeWithDb));

        stillingRepository.saveAll(stillinger.merge(NEW, CHANGED));

        log.info("Saved {} new and {} changed ads from xml-stilling total {}", stillinger.size(NEW), stillinger.size(CHANGED), stillinger.asList().size());

        return stillinger;
    }

    private Gruppe saveOnlyNewAndChangedGroupingStrategy(Stilling stilling) {
        return stillingRepository.findByKildeAndMediumAndExternalId(stilling.getKilde(), stilling.getMedium(), stilling.getExternalId())
                .map(dbStilling -> {
                    if (!dbStilling.getHash().equals(stilling.getHash()) || dbStilling.getAnnonseStatus() != AnnonseStatus.AKTIV) {
                        return CHANGED;
                    }
                    return UNCHANGED;
                })
                .orElse(NEW);
    }

    private Gruppe saveAllGroupingStrategy(Stilling stilling) {
        return stillingRepository.findByKildeAndMediumAndExternalId(stilling.getKilde(), stilling.getMedium(), stilling.getExternalId())
                .map(dbStilling -> CHANGED)
                .orElse(NEW);
    }

    private void mergeWithDb(Stilling stilling) {
        stillingRepository.findByKildeAndMediumAndExternalId(stilling.getKilde(), stilling.getMedium(), stilling.getExternalId())
                .ifPresent(stilling::merge);
    }

}
