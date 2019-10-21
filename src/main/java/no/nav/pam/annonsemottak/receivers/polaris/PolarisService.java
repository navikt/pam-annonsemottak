package no.nav.pam.annonsemottak.receivers.polaris;

import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.Medium;
import no.nav.pam.annonsemottak.receivers.common.rest.payloads.ResultsOnSave;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.receivers.fangst.AnnonseResult;
import no.nav.pam.annonsemottak.receivers.polaris.model.PolarisAd;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolarisService {

    private static final Logger LOG = LoggerFactory.getLogger(PolarisService.class);

    private final ExternalRunService externalRunService;
    private final PolarisConnector polarisConnector;
    private final AnnonseFangstService annonseFangstService;
    private final AnnonseMottakProbe probe;

    @Autowired
    public PolarisService(
            ExternalRunService externalRunService,
            PolarisConnector polarisConnector,
            AnnonseFangstService annonseFangstService,
            AnnonseMottakProbe probe) {
        this.externalRunService = externalRunService;
        this.polarisConnector = polarisConnector;
        this.annonseFangstService = annonseFangstService;
        this.probe = probe;
    }

    public ResultsOnSave fetchAndSaveLatest() throws IOException {
        long start = System.currentTimeMillis();

        ExternalRun externalRun = externalRunService.retrieveExternalRun(Kilde.POLARIS.value());
        if (externalRun == null) {
            externalRun = new ExternalRun(Kilde.POLARIS.value(), Medium.POLARIS.value(), LocalDateTime.now().minusMonths(2));
        }
        LOG.info("Start fetching Polaris ads updated since {}", externalRun.getLastRun());

        //Datetime in polaris is inconsistent, so giving it a little room for last updated date
        LocalDateTime newRunTime = LocalDateTime.now().minusDays(30);
        List<PolarisAd> polarisAdList = polarisConnector.fetchData(externalRun.getLastRun());

        List<Stilling> receivedList = polarisAdList.stream()
                .filter(a -> !isOneOfFiltered(a))
                .map(PolarisAdMapper::mapToStilling)
                .collect(Collectors.toList());
        List<String> receivedExternalIdList = receivedList.stream().map(Stilling::getExternalId).collect(Collectors.toList());
        LOG.info("Fetched {} ads from Polaris", receivedList.size());

        AnnonseResult annonseResult = annonseFangstService.retrieveAnnonseLists(receivedList, receivedExternalIdList,
                Kilde.POLARIS.value(), Medium.POLARIS.value());
        annonseFangstService.saveAll(annonseResult);

        LOG.info("Saved {} new, {} changed, {} stopped ads from Polaris",
                annonseResult.getNewList().size(),
                annonseResult.getModifyList().size(),
                annonseResult.getStopList().size());
        probe.addMetricsCounters(Kilde.POLARIS.toString(), "POLARIS", annonseResult.getNewList().size(), annonseResult.getStopList().size(), annonseResult.getDuplicateList().size(), annonseResult.getModifyList().size());
        externalRun.setLastRun(newRunTime);
        externalRunService.save(externalRun);

        return new ResultsOnSave(receivedList.size(), annonseResult.getNewList().size(), System.currentTimeMillis() - start);
    }

    private boolean isOneOfFiltered(PolarisAd ad){
        return StringUtils.isNotEmpty(ad.externalSystemUrl)
                && ad.externalSystemUrl.contains("jobbnorge");
    }

}
