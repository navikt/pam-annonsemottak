package no.nav.pam.annonsemottak.receivers.xmlstilling.scheduler;

import net.javacrumbs.shedlock.core.SchedulerLock;
import no.nav.pam.annonsemottak.receivers.xmlstilling.XmlStillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static no.nav.pam.unleash.UnleashProvider.toggle;


@Component
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class XmlStillingSchedulerTask {

    private static final Logger LOG = LoggerFactory.getLogger(XmlStillingSchedulerTask.class);

    private final XmlStillingService xmlStillingService;

    @Inject
    public XmlStillingSchedulerTask(XmlStillingService xmlStillingService) {
        this.xmlStillingService = xmlStillingService;
    }

    @Scheduled(cron = "0 */30 * * * *")
    @SchedulerLock(name = "saveLatestAdsFromXmlStilling")
    public void saveLatestAds() {
        if(toggle("pam.schedule.fetch.from.xmlstilling").isDisabled()) {
            LOG.info("Xml Stilling scheduling disabled");
            return;
        }
        LOG.info("Xml Stilling scheduling enabled");

        LOG.info("Running scheduled job for saving the latest job ads fetched from Xml-Stilling.");

        try {
            xmlStillingService.updateLatest(false);
            LOG.info("Scheduler for Xml-Stilling is finished");
        } catch (Exception e) {
            LOG.error("Unable to save results from Xml-Stilling", e);
        }
    }
}
