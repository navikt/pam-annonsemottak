package no.nav.pam.annonsemottak.annonsemottak.polaris;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.annonsemottak.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;

@Service
public class PolarisService {

    private static final Logger LOG = LoggerFactory.getLogger(PolarisService.class);

    private final ExternalRunService externalRunService;
    private final MeterRegistry meterRegistry;
    private final PolarisConnector polarisConnector;
    private final AnnonseFangstService annonseFangstService;

    @Autowired
    public PolarisService(ExternalRunService externalRunService,
                          MeterRegistry meterRegistry,
                          PolarisConnector polarisConnector,
                          AnnonseFangstService annonseFangstService) {
        this.externalRunService = externalRunService;
        this.meterRegistry = meterRegistry;
        this.polarisConnector = polarisConnector;
        this.annonseFangstService = annonseFangstService;
    }

    public ResultsOnSave fetchAndSaveLatest() throws IOException {
        long start = System.currentTimeMillis();

        ExternalRun externalRun = externalRunService.retrieveExternalRun(Kilde.POLARIS.value());
        if (externalRun == null) {
            externalRun = new ExternalRun(Kilde.POLARIS.value(), Medium.POLARIS.value(), LocalDateTime.now().minusMonths(2));
        }
        LOG.info("Start fetching Polaris ads updated since {}", externalRun.getLastRun());

        LocalDateTime newRunTime = LocalDateTime.now();
        List<PolarisAd> polarisAdList = polarisConnector.fetchData(externalRun.getLastRun());

        List<Stilling> receivedList = polarisAdList.stream().map(PolarisAdMapper::mapToStilling).collect(Collectors.toList());
        List<String> receivedExternalIdList = receivedList.stream().map(Stilling::getExternalId).collect(Collectors.toList());
        LOG.info("Fetched {} ads from Polaris", receivedList.size());

        AnnonseResult annonseResult = annonseFangstService.retrieveAnnonseLists(receivedList, receivedExternalIdList,
                Kilde.POLARIS.value(), Medium.POLARIS.value());
        annonseFangstService.handleDuplicates(annonseResult);
        annonseFangstService.saveAll(annonseResult);

        LOG.info("Saved {} new, {} changed, {} stopped ads from Polaris",
                annonseResult.getNewList().size(),
                annonseResult.getModifyList().size(),
                annonseResult.getStopList().size());

        meterRegistry.gauge(ADS_COLLECTED_POLARIS_TOTAL, receivedList.size());
        meterRegistry.gauge(ADS_COLLECTED_POLARIS_NEW, annonseResult.getNewList().size());
        meterRegistry.gauge(ADS_COLLECTED_POLARIS_REJECTED, annonseResult.getModifyList().size() - annonseResult.getNewList().size());
        meterRegistry.gauge(ADS_COLLECTED_POLARIS_CHANGED, annonseResult.getModifyList().size());
        meterRegistry.gauge(ADS_COLLECTED_POLARIS_STOPPED, annonseResult.getStopList().size());

        externalRun.setLastRun(newRunTime);
        externalRunService.save(externalRun);

        return new ResultsOnSave(receivedList.size(), annonseResult.getNewList().size(), System.currentTimeMillis() - start);
    }


}
