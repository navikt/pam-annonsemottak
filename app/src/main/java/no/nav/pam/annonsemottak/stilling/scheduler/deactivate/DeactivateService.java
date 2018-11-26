package no.nav.pam.annonsemottak.stilling.scheduler.deactivate;


import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeactivateService {

    private static final Logger LOG = LoggerFactory.getLogger(DeactivateService.class);

    private static final int EXPIRES_IN_DAYS = 30; //TODO: Clarify later

    private final StillingRepository repository;

    @Inject
    public DeactivateService(StillingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void deactivateExpired() {
        List<Stilling> activeAds = repository.findByAnnonseStatus(AnnonseStatus.AKTIV);
        List<Stilling> expiredAds = new ArrayList<>();

        for (Stilling s : activeAds) {
            if (s.getExpires() != null) {
                if (s.getExpires().isBefore(LocalDateTime.now())) {
                    s.deactivate();
                    expiredAds.add(s);
                }
            } else {
                if(s.getCreated().plusDays(EXPIRES_IN_DAYS).isAfter(LocalDateTime.now())){
                    s.deactivate();
                    expiredAds.add(s);
                }
            }
        }

        LOG.info("Deactivated {} ads that have passed their expiry date", expiredAds.size());
        repository.saveAll(expiredAds);
    }
}
