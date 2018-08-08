package no.nav.pam.annonsemottak.annonsemottak.scheduler.resetSaksbehandler;

import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Status;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResetSaksbehandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(ResetSaksbehandlerService.class);

    private static final int RESET_AFTER_DAYS = 7;

    private final StillingRepository repository;

    @Inject
    public ResetSaksbehandlerService(StillingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void resetSaksbehandler() {
        List<Stilling> ads = repository.findBySaksbehandlingStatusAndAnnonseStatus(Status.UNDER_ARBEID, AnnonseStatus.AKTIV);

        if (ads != null) {
            List<Stilling> resetList = ads.stream()
                    .filter(s -> s.getUpdated().plusDays(RESET_AFTER_DAYS).isBeforeNow())
                    .collect(Collectors.toList());

            for (Stilling s : resetList) {
                s.reset();
            }

            LOG.info("Reset status and saksbehandler for {} ads", resetList.size());
            repository.saveAll(resetList);
        }
    }
}
