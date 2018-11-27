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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DexiAnnonseFangstService {

    private final StillingRepository stillingRepository;
    private final DuplicateHandler duplicateHandler;

    private static final Logger LOG = LoggerFactory.getLogger(DexiAnnonseFangstService.class);

    @Inject
    public DexiAnnonseFangstService(StillingRepository repository, DuplicateHandler duplicateHandler) {
        this.stillingRepository = repository;
        this.duplicateHandler = duplicateHandler;
    }

    public AnnonseResult retrieveAnnonseLists(List<Stilling> receiveList, String kilde, String medium) {
        LOG.info("Running retrieve Annonseresult for {} {} with received list {} ", kilde, medium, receiveList.size());

        List<Stilling> activeList = stillingRepository.findByKildeAndMediumAndAnnonseStatus(kilde, medium, AnnonseStatus.AKTIV);

        receiveList = filterExternalIdDuplicates(receiveList);
        AnnonseResult result = prepareAnnonseResultFromReceiveList(receiveList, activeList);
        duplicateHandler.markDuplicates(result);
        return result;
    }

    // Dexi sometimes give us duplicates
    private List<Stilling> filterExternalIdDuplicates(List<Stilling> receiveList) {
        HashMap<String,Stilling> jobMap = new HashMap<>();
        for (Stilling receive: receiveList) {
            if (!jobMap.containsKey(receive.getExternalId())) {
                jobMap.put(receive.getExternalId(), receive);
            }
            else {
                LOG.warn("ExternalID {} is not unique for this list, please double check!!", receive.getExternalId());
            }
        }
        return new ArrayList<>(jobMap.values());
    }

    private AnnonseResult prepareAnnonseResultFromReceiveList(List<Stilling> receiveList, List<Stilling> activeList) {
        AnnonseResult result = new AnnonseResult();

        Map<String, Stilling> receiveMap = receiveList.stream().collect(Collectors.toMap(Stilling::getExternalId, Function.identity()));
        Map<String, Stilling> activeMap = activeList.stream().collect(Collectors.toMap(Stilling::getExternalId, Function.identity()));
        for (Stilling active : activeList) {
            if(!receiveMap.containsKey(active.getExternalId())) {
                if(active.getExpires().isBefore(LocalDateTime.now())){
                    active.deactivate();
                    result.getExpiredList().add(active);
                }
                else {
                    active.stop();
                    result.getStopList().add(active);
                }
            }
        }
        for (Stilling receive : receiveList) {
            if (activeMap.containsKey(receive.getExternalId())) {
                result.handleIfModifiedAd(activeMap, receive);
            } else {
                Optional<Stilling> notActive = stillingRepository.findByKildeAndMediumAndExternalId(receive.getKilde(),
                    receive.getMedium(), receive.getExternalId());
                result.handleIfNotActiveAd(receive, notActive);
            }
        }

        return result;
    }

    @Transactional
    public void saveAll(AnnonseResult annonseResult) {
        stillingRepository.saveAll(annonseResult.getAll());
        LOG.info("Persisted: {}", annonseResult);
    }

}
