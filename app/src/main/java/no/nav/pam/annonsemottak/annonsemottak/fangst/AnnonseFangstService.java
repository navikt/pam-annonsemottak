package no.nav.pam.annonsemottak.annonsemottak.fangst;

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
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class AnnonseFangstService {

    private final StillingRepository stillingRepository;
    private final DuplicateHandler duplicateHandler;

    private static final Logger LOG = LoggerFactory.getLogger(AnnonseFangstService.class);

    @Inject
    public AnnonseFangstService(StillingRepository repository, DuplicateHandler duplicateHandler) {
        this.stillingRepository = repository;
        this.duplicateHandler = duplicateHandler;
    }

    @Transactional(readOnly = true)
    public AnnonseResult retrieveAnnonseLists(List<Stilling> receiveList, Collection<String> allActiveExternalIds, String kilde, String medium) {
        LOG.info("Annonsefangstservice får inn {} stillinger som skal behandles", receiveList.size());
        List<Stilling> activeList = stillingRepository.findByKildeAndMediumAndAnnonseStatus(kilde, medium, AnnonseStatus.AKTIV);
        return prepareAnnonseResultFromReceiveList(receiveList, allActiveExternalIds, activeList);
    }

    public void handleDuplicates(AnnonseResult annonseResult) {
        LOG.info("Annonsefangstservice, før duplikatkontroll,  annonseresult: {}", annonseResult.toString());
        duplicateHandler.markDuplicates(annonseResult);
        LOG.info("Annonsefangstservice, etter duplikatkontroll,  annonseresult: {}", annonseResult.toString());
        LOG.info("Found {} duplicated ad", annonseResult.getDuplicateList().size());
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
                Stilling notActive = stillingRepository.findByKildeAndMediumAndExternalId(receive.getKilde(),
                        receive.getMedium(), receive.getExternalId());
                annonseResult.handleIfNotActiveAd(receive, notActive);
            }
        }

        LOG.info("Annonsefangstservice, etter behandling av updated og created, antall {}, annonseresult: {}", receiveList.size(), annonseResult.toString());
        return annonseResult;
    }


    public void saveAll(AnnonseResult annonseResult) {
        LOG.info("Annonsefangstservice, før save,  annonseresult: {}", annonseResult.toString());
        annonseResult.getAll().stream().forEach(stilling -> saveOne(stilling));
        LOG.info("Annonsefangstservice, etter save,  annonseresult: {}", annonseResult.toString());
    }

    @Transactional
    private void saveOne(Stilling s) {
        try {
            stillingRepository.save(s);
        } catch (Exception e) {
            LOG.error("Error while saving ad {} from source {}. Error: {}", s.getUuid(), s.getKilde(), e.getMessage());
        }
    }
}
