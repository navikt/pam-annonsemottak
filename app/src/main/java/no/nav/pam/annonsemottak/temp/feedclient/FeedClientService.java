package no.nav.pam.annonsemottak.temp.feedclient;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import no.nav.pam.feed.client.FeedConnector;
import no.nav.pam.feed.taskscheduler.FeedTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_COLLECTED_FEED_FAILED;
import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_COLLECTED_FEED_OK;

@Service
public class FeedClientService {

    public static final String TASK_NAME = "feedclient.annonsemottak";

    private static final Logger LOG = LoggerFactory.getLogger(FeedClientService.class);

    private final FeedTaskService feedTaskService;
    private final FeedConnector feedConnector;
    private final String feedUrl;
    private final MeterRegistry meterRegistry;
    private final StillingRepository stillingRepository;

    @Autowired
    public FeedClientService(
            FeedTaskService feedTaskService,
            FeedConnector feedConnector,
            MeterRegistry meterRegistry,
            @Value("${feed.annonsemottak.url}") String feedUrl,
            StillingRepository stillingRepository) {

        this.feedTaskService = feedTaskService;
        this.feedConnector = feedConnector;
        this.meterRegistry = meterRegistry;
        this.feedUrl = feedUrl;
        this.stillingRepository = stillingRepository;
    }

    public void fetchAndSaveLatestAds() throws IOException {

        Optional<LocalDateTime> lastRunDate = feedTaskService.fetchLastRunDateForJob(TASK_NAME);
        long lastUpdateDate = lastRunDate.map(d -> d.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).orElse(0L);

        lastRunDate.ifPresent(d -> LOG.info("Fetching ads updated after {} from annonsemottak", d));

        List<StillingFeedItem> feedList = feedConnector.fetchContentList(feedUrl, lastUpdateDate, StillingFeedItem.class);
        feedList.sort(Comparator.comparing(o -> o.updated));

        fetchAndSaveFromFeed(feedList);

        if (!feedList.isEmpty()) {
            LocalDateTime lastUpdatedDate = feedList.get(feedList.size() - 1).updated;
            feedTaskService.save(TASK_NAME, lastUpdatedDate);
            LOG.info("Saved new last run time for task {} as {} ", TASK_NAME, lastUpdatedDate);
        }
    }

    public void fetchAndSaveOneAd(String uuid) throws IOException {

        List<StillingFeedItem> feedList = feedConnector.fetchContentList(feedUrl + "/" + uuid, 0, StillingFeedItem.class);

        fetchAndSaveFromFeed(feedList);
    }

    private void fetchAndSaveFromFeed(List<StillingFeedItem> feedList) {
        List<Stilling> stillingList = feedList.stream()
                .distinct()
                .map(StillingFeedItemMapper::toStilling)
                .filter(Objects::nonNull) // remove this later
                .collect(Collectors.toList());

        saveAdsIndividually(stillingList);
    }

    private void saveAdsIndividually(List<Stilling> ads) {

        int successCount = 0;
        int failCount = 0;

        for (Stilling a : ads) {
            try {
                Stilling inDb = stillingRepository.findByUuid(a.getUuid());
                if (inDb != null) {
                    LOG.debug("Saving updated ad with uuid {}", a.getUuid());
                    a.setId(inDb.getId());
                    stillingRepository.save(a);
                } else {
                    LOG.debug("Saving new ad with uuid {}", a.getUuid());
                    stillingRepository.save(a);
                }
                successCount++;
            } catch (Exception e) {
                LOG.error("Failed saving ad with uuid {} due to {}", a.getUuid(), e);
                failCount++;
            }
        }

        meterRegistry.gauge(ADS_COLLECTED_FEED_OK, successCount);
        meterRegistry.gauge(ADS_COLLECTED_FEED_FAILED, failCount);
        LOG.info("Saved a list of {} ads. Successfully saved {} and failed {} ads", ads.size(), successCount, failCount);
    }

    public List<StillingFeedItem> fetchFeed() throws IOException {
        return feedConnector.fetchContentList(feedUrl, -1L, StillingFeedItem.class);
    }
}
