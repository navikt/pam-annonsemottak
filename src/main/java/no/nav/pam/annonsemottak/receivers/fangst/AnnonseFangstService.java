package no.nav.pam.annonsemottak.receivers.fangst;

import no.nav.pam.annonsemottak.outbox.StillingOutboxService;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AnnonseFangstService {

    private final StillingRepository stillingRepository;
    private final StillingOutboxService stillingOutboxService;

    private static final Logger LOG = LoggerFactory.getLogger(AnnonseFangstService.class);

    @Autowired
    public AnnonseFangstService(StillingRepository repository,  StillingOutboxService stillingOutboxService) {
        this.stillingRepository = repository;
        this.stillingOutboxService = stillingOutboxService;
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
                    LOG.info("Stopping ad {} as it is not in active list", active.getUuid());
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


    @Transactional
    public void saveAll(AnnonseResult annonseResult) {
        LOG.info("Annonsefangstservice, før save,  annonseresult: {}", annonseResult.toString());
        annonseResult.getAll().forEach(this::saveOne);
        LOG.info("Annonsefangstservice, etter save,  annonseresult: {}", annonseResult.toString());
    }

    @Transactional
    public void saveOne(Stilling s) {
        try {
            Stilling lagretStilling = stillingRepository.save(s);
            stillingOutboxService.lagreTilOutbox(lagretStilling);
        } catch (Exception e) {
            LOG.error("Error while saving ad {} from source {}. Error: {} - Full error: {}", s.getUuid(), s.getKilde(), e.getMessage(), e.toString());
        }
    }
}
