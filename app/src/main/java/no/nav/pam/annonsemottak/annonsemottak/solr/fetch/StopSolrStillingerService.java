package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
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
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_DEACTIVATED_SOLR;

@Service
public class StopSolrStillingerService {

    private static final Logger LOG = LoggerFactory.getLogger(StopSolrStillingerService.class);

    private final MeterRegistry meterRegistry;
    private final StillingRepository stillingRepository;

    @Autowired
    public StopSolrStillingerService(StillingRepository stillingRepository,
                                     MeterRegistry meterRegistry) {
        this.stillingRepository = stillingRepository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional
    public void findAndStopOldSolrStillinger(List<Stilling> savedAds) {
        List<Stilling> activeAds = stillingRepository.findByKildeAndAnnonseStatus(Kilde.STILLINGSOLR.value(), AnnonseStatus.AKTIV);

        // Stopping ads if they're removed from stillingsolr, to prevent them getting republished
        Set<String> savedUuids = savedAds.stream().map(s -> s.getUuid()).collect(Collectors.toSet());
        List<Stilling> stoppedAds = activeAds.stream()
                .filter(s -> !savedUuids.contains(s.getUuid()))
                .map(Stilling::stop)
                .collect(Collectors.toList());

        stillingRepository.saveAll(stoppedAds);

        meterRegistry.gauge(ADS_DEACTIVATED_SOLR, stoppedAds.size());

        LOG.info("Stopped {} inactive ads from solr", stoppedAds.size());
    }
}
