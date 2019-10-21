package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.CHANGED;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.NEW;

@Service
public class XmlStillingService {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingService.class);

    private final XmlStillingConnector xmlStillinger;
    private final ExternalRunFacade externalRuns;
    private final AnnonseMottakProbe probe;
    private final StillingRepositoryFacade repository;


    @Inject
    public XmlStillingService(
            final XmlStillingConnector connector,
            ExternalRunFacade externalRuns,
            StillingRepositoryFacade stillingRepository,
            AnnonseMottakProbe probe) {

        this.xmlStillinger = connector;
        this.externalRuns = externalRuns;
        this.repository = stillingRepository;
        this.probe = probe;
    }

    public List<Stilling> updateLatest(boolean saveAllFetchedAds) {

        Stillinger stillinger = externalRuns.decorate(update(saveAllFetchedAds));

        probe.addMetricsCounters(Kilde.XML_STILLING.toString(), "XML_STILLING", stillinger.size(NEW), 0, 0, stillinger.size(CHANGED));

        return stillinger.asList();

    }

    Function<LocalDateTime, Stillinger> update(boolean saveAllFetchedAds) {
        return nextRunStart -> {

            List<Stilling> nyeStillinger = xmlStillinger.fetchFrom(nextRunStart);

            Function<Stilling, Gruppe> grupperingStrategi = saveAllFetchedAds ? repository::saveAllGroupingStrategy : repository::saveOnlyNewAndChangedGroupingStrategy;

            return repository.updateStillinger(nyeStillinger, grupperingStrategi);

        };
    }

}
