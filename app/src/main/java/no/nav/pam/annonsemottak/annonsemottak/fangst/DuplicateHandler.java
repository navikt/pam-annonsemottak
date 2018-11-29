package no.nav.pam.annonsemottak.annonsemottak.fangst;

import no.nav.pam.annonsemottak.annonsemottak.solr.SolrService;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.annonsemottak.solr.fetch.StillingSolrBeanFieldNames;
import no.nav.pam.annonsemottak.stilling.IllegalSaksbehandlingCommandException;
import no.nav.pam.annonsemottak.stilling.OppdaterSaksbehandlingCommand;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Component
public class DuplicateHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DuplicateHandler.class);
    private static final long BACKOFF=500;
    private final SolrService solrService;

    @Inject
    public DuplicateHandler(SolrService solrService) {
        this.solrService = solrService;
    }

    void markDuplicates(AnnonseResult result) {
        for (Stilling stilling : result.getNewList()) {
            try {
                List<StillingSolrBean> stillingSolrBeans = search(stilling);
                if (stillingSolrBeans.size() > 0) {
                    updateDuplicateStatus(stilling, stillingSolrBeans);
                    result.getDuplicateList().add(stilling);
                }
                Thread.sleep(BACKOFF);
            }
            catch (Exception e) {
                LOG.error("Got exception while searching solr",e);
            }
        }
        for (Stilling dupe : result.getDuplicateList()) {
            result.getNewList().remove(dupe);
        }
    }

    private void updateDuplicateStatus(Stilling stilling, List<StillingSolrBean> stillingSolrBeans) {
        StillingSolrBean stillingSolrBean = stillingSolrBeans.get(0);
        LOG.debug("Got duplicate arbeidsgiver: " + stillingSolrBean.getArbeidsgivernavn() +
                " tittel: "+ stillingSolrBean.getTittel() + " id: " + stillingSolrBean.getId());
        stilling.rejectAsDuplicate(stillingSolrBean.getId());
        try {
            stilling.oppdaterMed(new OppdaterSaksbehandlingCommand(Collections.singletonMap("saksbehandler", "System")));
        } catch (IllegalSaksbehandlingCommandException e) {
            throw new IllegalStateException("Unexpceted error when updating ad reportee", e);
        }
    }

    private List<StillingSolrBean> search(Stilling stilling) {
        HashMap<String,String> params = new HashMap<>();
        LOG.debug("Searching tittel: {}, arbeidsgiver: {}", stilling.getTitle(), stilling.getArbeidsgiver().get().asString());
        params.put(StillingSolrBeanFieldNames.ARBEIDSGIVERNAVN, stilling.getArbeidsgiver().get().asString());
        params.put(StillingSolrBeanFieldNames.TITTEL, stilling.getTitle());
        return solrService.searchStillinger(params);

    }

}
