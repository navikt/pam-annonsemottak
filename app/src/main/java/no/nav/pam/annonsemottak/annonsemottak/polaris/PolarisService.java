package no.nav.pam.annonsemottak.annonsemottak.polaris;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;
import no.nav.pam.annonsemottak.annonsemottak.Medium;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.annonsemottak.externalRun.ExternalRunService;
import no.nav.pam.annonsemottak.annonsemottak.fangst.AnnonseFangstService;
import no.nav.pam.annonsemottak.annonsemottak.polaris.model.PolarisAd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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

    public void fetchLatest() {

        ExternalRun externalRun = externalRunService.retrieveExternalRun(Kilde.POLARIS.value());
        if (externalRun == null) {
            externalRun = new ExternalRun(Kilde.POLARIS.value(), Medium.POLARIS.value(), LocalDateTime.now().minusMonths(2));
        }
        LOG.info("Start fetching Polaris ads updated since {}", externalRun.getLastRun());

        try {
            LocalDateTime newRunTime = LocalDateTime.now();
            List<PolarisAd> polarisAdList = polarisConnector.fetchData(externalRun.getLastRun());



            externalRun.setLastRun(newRunTime);
            //externalRunService.save(externalRun);
            LOG.info("Last run timestamp for Polaris fetch is {}", newRunTime);
        } catch (IOException e) {
            LOG.error("Failed fetching Polaris ads", e);
        }


    }


}
