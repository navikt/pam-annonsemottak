package no.nav.pam.annonsemottak.receivers.fangst;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;
import static no.nav.pam.annonsemottak.app.metrics.MetricNames.ADS_COLLECTED_CHANGED;

@Component
public class AnnonseFangstService {

    private final StillingRepository stillingRepository;
    private final MeterRegistry meterRegistry;

    private static final Logger LOG = LoggerFactory.getLogger(AnnonseFangstService.class);

    @Inject
    public AnnonseFangstService(StillingRepository repository, MeterRegistry meterRegistry) {
        this.stillingRepository = repository;
        this.meterRegistry = meterRegistry;
    }

    @Transactional(readOnly = true)
    public AnnonseResult retrieveAnnonseLists(List<Stilling> receiveList, Collection<String> allActiveExternalIds, String kilde, String medium) {
        LOG.info("Annonsefangstservice får inn {} stillinger som skal behandles", receiveList.size());
        List<Stilling> activeList = stillingRepository.findByKildeAndMediumAndAnnonseStatus(kilde, medium, AnnonseStatus.AKTIV);
        return prepareAnnonseResultFromReceiveList(receiveList, allActiveExternalIds, activeList);
    }

    private AnnonseResult prepareAnnonseResultFromReceiveList(List<Stilling> receiveList,
                                                              Collection<String> allActiveExternalIds,
                                                              List<Stilling> activeList) {

        AnnonseResult annonseResult = new AnnonseResult();

        // Determine ads that are deleted in source i.e. stopped ads
        for (Stilling active : activeList) {
            if (!allActiveExternalIds.contains(active.getExternalId())) {

                // An ad can be manually deleted in source or have expired
                if (active.getExpires() != null && active.getExpires().isBefore(LocalDateTime.now())) {
                    LOG.debug("Ad {} with expiration date {} has expired", active.getExternalId(), active.getExpires());
                    active.deactivate();
                    annonseResult.getExpiredList().add(active);
                } else {
                    active.stop();
                    annonseResult.getStopList().add(active);
                }
            }
        }

        // Determine ads which are either updated or created in source i.e. changed or new ads
        Map<String, Stilling> activeMap = activeList.stream().collect(Collectors.toMap(Stilling::getExternalId, Function.identity()));

        LOG.info("Annonsefangstservice, før behandling av updated og created, antall {}, annonseresult: {}", receiveList.size(), annonseResult.toString());
        for (Stilling receive : receiveList) {
            if (activeMap.containsKey(receive.getExternalId())) {
                annonseResult.handleIfModifiedAd(activeMap, receive);
            } else {
                Optional<Stilling> notActive = stillingRepository.findByKildeAndMediumAndExternalId(receive.getKilde(),
                        receive.getMedium(), receive.getExternalId());
                annonseResult.handleIfNotActiveAd(receive, notActive);
            }
        }

        LOG.info("Annonsefangstservice, etter behandling av updated og created, antall {}, annonseresult: {}", receiveList.size(), annonseResult.toString());
        return annonseResult;
    }


    public void saveAll(AnnonseResult annonseResult) {
        LOG.info("Annonsefangstservice, før save,  annonseresult: {}", annonseResult.toString());
        annonseResult.getAll().forEach(this::saveOne);
        LOG.info("Annonsefangstservice, etter save,  annonseresult: {}", annonseResult.toString());
    }

    private void saveOne(Stilling s) {
        try {
            stillingRepository.save(s);
        } catch (Exception e) {
            LOG.error("Error while saving ad {} from source {}. Error: {}", s.getUuid(), s.getKilde(), e.getMessage());
        }
    }

    public void addMetricsCounters(Kilde kilde, String origin, int newSize, int stopSize, int dupSize, int modifySize) {
        String [] tags = {"source", kilde.toString(), "origin", origin};
        addMetricsCounters(newSize, stopSize, dupSize, modifySize, tags);
    }

    private void addMetricsCounters(int newSize, int stopSize, int dupSize, int modifySize, String ... tags) {
        meterRegistry.counter(ADS_COLLECTED_NEW, tags).increment(newSize);
        meterRegistry.counter(ADS_COLLECTED_STOPPED, tags).increment(stopSize);
        meterRegistry.counter(ADS_COLLECTED_DUPLICATED,tags).increment(dupSize);
        meterRegistry.counter(ADS_COLLECTED_CHANGED, tags).increment(modifySize);
    }
}
