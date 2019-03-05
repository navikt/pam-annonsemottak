package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.*;

@Service
class StillingRepositoryFacade {

    private static final Logger log = LoggerFactory.getLogger(StillingRepositoryFacade.class);

    private final StillingRepository stillingRepository;

    @Inject
    StillingRepositoryFacade(StillingRepository stillingRepository) {
        this.stillingRepository = stillingRepository;
    }


    Stillinger updateStillinger(
            List<Stilling> nyeStillinger,
            Function<Stilling, Gruppe> grupperingStrategi) {

        Stillinger stillinger = new Stillinger(nyeStillinger, grupperingStrategi);

        log.debug("Siste stilling mottat {} fra xml-stilling", stillinger.latestDate());

        stillinger.get(NEW).forEach(stillingRepository::save);

        // Cannot save all in case we get duplicate IDs incoming, which may happen if the ad is updated
        stillinger.get(CHANGED).stream().peek(this::mergeWithDb).forEach(stillingRepository::save);

        log.info("Saved {} new and {} changed ads from xml-stilling total {}", stillinger.size(NEW), stillinger.size(CHANGED), stillinger.asList().size());

        return stillinger;
    }

    Gruppe saveOnlyNewAndChangedGroupingStrategy(Stilling stilling) {
        return stillingRepository.findByKildeAndMediumAndExternalId(stilling.getKilde(), stilling.getMedium(), stilling.getExternalId())
                .map(dbStilling -> {
                    if (!dbStilling.getHash().equals(stilling.getHash()) || dbStilling.getAnnonseStatus() != AnnonseStatus.AKTIV) {
                        return CHANGED;
                    }
                    return UNCHANGED;
                })
                .orElse(NEW);
    }

    Gruppe saveAllGroupingStrategy(Stilling stilling) {
        return stillingRepository.findByKildeAndMediumAndExternalId(stilling.getKilde(), stilling.getMedium(), stilling.getExternalId())
                .map(dbStilling -> CHANGED)
                .orElse(NEW);
    }

    private void mergeWithDb(Stilling stilling) {
        stillingRepository.findByKildeAndMediumAndExternalId(stilling.getKilde(), stilling.getMedium(), stilling.getExternalId())
                .ifPresent(stilling::merge);
    }

}
