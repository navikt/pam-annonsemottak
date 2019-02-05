package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class XmlStillingService {

    private static final Logger log = LoggerFactory.getLogger(XmlStillingService.class);

    private final XmlStillingConnector xmlStillinger;
    private final ExternalRunFacade externalRuns;
    private final XmlStillingMetrics metrics;
    private final StillingRepositoryFacade repository;


    @Inject
    public XmlStillingService(
            final XmlStillingConnector connector,
            ExternalRunFacade externalRuns,
            StillingRepositoryFacade stillingRepository,
            XmlStillingMetrics metrics) {

        this.xmlStillinger = connector;
        this.externalRuns = externalRuns;
        this.repository = stillingRepository;
        this.metrics = metrics;
    }

    public List<Stilling> updateLatest(boolean saveAllFetchedAds) {

        Stillinger stillinger = externalRuns.decorate(update(saveAllFetchedAds));

        metrics.registerFor(stillinger);

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
