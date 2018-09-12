package no.nav.pam.annonsemottak.feed;


import no.nav.pam.annonsemottak.markdown.MarkdownToHtmlConverter;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StillingFeedService {

    private final StillingRepository stillingRepository;

    @Inject
    public StillingFeedService(StillingRepository stillingRepository) {
        this.stillingRepository = stillingRepository;
    }

    public Page<Stilling> findStillingUpdatedAfter(LocalDateTime updatedDate, Pageable pageable) {

        return stillingRepository.findUpdatedAfter(updatedDate, pageable)
                .map(s -> {
                            s.setJobDescription(MarkdownToHtmlConverter.parse(s.getAnnonsetekst()));
                            s.setEmployerDescription(MarkdownToHtmlConverter.parse(s.getArbeidsgiveromtale()));
                            return s;
                        }
                );
    }

    /**
     * Wrap a single Stilling in Page and return as a feed of one element.
     */
    public Page<Stilling> findStilling(String uuid) {

        List<Stilling> stillingList = new ArrayList();
        Stilling stilling = stillingRepository.findByUuid(uuid);
        stilling.setJobDescription(MarkdownToHtmlConverter.parse(stilling.getAnnonsetekst()));
        stilling.setEmployerDescription(MarkdownToHtmlConverter.parse(stilling.getArbeidsgiveromtale()));

        if (stilling != null) {
            stillingList.add(stilling);
        }

        return new PageImpl<>(stillingList);
    }

    public Page<Stilling> findAllActive(Pageable pageable) {
        return stillingRepository.findAll(pageable).map(s -> {
            s.setJobDescription(MarkdownToHtmlConverter.parse(s.getAnnonsetekst()));
            s.setEmployerDescription(MarkdownToHtmlConverter.parse(s.getArbeidsgiveromtale()));
            return s;
        });
    }
}
