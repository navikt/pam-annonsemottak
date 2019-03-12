package no.nav.pam.annonsemottak.receivers.solr.fetch;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_COLLECTED_STOPPED;
import static no.nav.pam.annonsemottak.receivers.solr.fetch.SolrFetchService.fraArbeidsgiver;
import static no.nav.pam.unleash.UnleashProvider.toggle;


@Service
public class StopSolrStillingerService {

    private static final Logger LOG = LoggerFactory.getLogger(StopSolrStillingerService.class);

    private final MeterRegistry meterRegistry;
    private final StillingRepository stillingRepository;

    /**
     * @deprecated Fjern når alle solr-stillinger er utløpt. Da er denne ikke akutell lenger
     */
    @Deprecated
    private Predicate<Stilling> ignoreOverfortFraArbeidsgiver = s -> {

        if(toggle("pam.schedule.fetch.from.xmlstilling").isDisabled()) {
            return !s.getMedium().equals(fraArbeidsgiver);
        }

        return true; // accept all if not not fra arbeidsgiver and toggle not disabled
    };

    @Autowired
    public StopSolrStillingerService(StillingRepository stillingRepository,
                                     MeterRegistry meterRegistry) {
        this.stillingRepository = stillingRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public void findAndStopOldSolrStillinger(List<Stilling> allFetchedAds) {
        List<Stilling> activeAds = stillingRepository.findByKildeAndAnnonseStatus(Kilde.STILLINGSOLR.value(), AnnonseStatus.AKTIV);

        // Stopping ads if they're removed from stillingsolr, to prevent them getting republished
        Set<String> savedUuids = allFetchedAds.stream().map(Stilling::getUuid).collect(Collectors.toSet());
        List<Stilling> stoppedAds = activeAds.stream()
                .filter(ignoreOverfortFraArbeidsgiver)
                .filter(s -> !savedUuids.contains(s.getUuid()))
                .map(Stilling::stop)
                .collect(Collectors.toList());

        stillingRepository.saveAll(stoppedAds);

        String [] tags = {"source", Kilde.STILLINGSOLR.toString(), "origin", "STILLINGSOLR"};
        meterRegistry.counter(ADS_COLLECTED_STOPPED,tags).increment(stoppedAds.size());
        LOG.info("Stopped {} inactive ads from solr", stoppedAds.size());
    }
}
