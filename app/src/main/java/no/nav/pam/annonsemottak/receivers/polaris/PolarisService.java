package no.nav.pam.annonsemottak.receivers.polaris;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
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

        meterRegistry.counter(ADS_COLLECTED_POLARIS, asList(
                Tag.of(ADS_COLLECTED_POLARIS_TOTAL, Integer.toString(receivedList.size())),
                Tag.of(ADS_COLLECTED_POLARIS_NEW, Integer.toString(annonseResult.getNewList().size())),
                Tag.of(ADS_COLLECTED_POLARIS_REJECTED, Integer.toString(annonseResult.getModifyList().size() - annonseResult.getNewList().size())),
                Tag.of(ADS_COLLECTED_POLARIS_CHANGED, Integer.toString(annonseResult.getModifyList().size())),
                Tag.of(ADS_COLLECTED_POLARIS_STOPPED, Integer.toString(annonseResult.getStopList().size())))).increment();

        externalRun.setLastRun(newRunTime);
        externalRunService.save(externalRun);

        return new ResultsOnSave(receivedList.size(), annonseResult.getNewList().size(), System.currentTimeMillis() - start);
    }


}
