package no.nav.pam.annonsemottak.annonsemottak.fangst;

import no.nav.pam.annonsemottak.annonsemottak.solr.SolrService;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.stilling.Status;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DuplicateHandlerTest {

    @Test
    public void testSystemReporteeSetWhenMarkingAsDuplicate() {
        SolrService solrService = mock(SolrService.class);
        StillingSolrBean existing = new StillingSolrBean();
        existing.setId(1000);
        existing.setArbeidsgivernavn("ACME Corp.");
        existing.setTittel("Job");
        when(solrService.searchStillinger(any())).thenReturn(Collections.singletonList(existing));

        DuplicateHandler handler = new DuplicateHandler(solrService);

        AnnonseResult result = new AnnonseResult();
        result.getNewList().add(StillingTestdataBuilder.enkelStilling().arbeidsgiver("ACME Corp.").tittel("Job").build());

        handler.markDuplicates(result);

        assertThat(result.getDuplicateList().size()).isEqualTo(1);

        Stilling duplicate = result.getDuplicateList().get(0);
        assertThat(duplicate.getSaksbehandler().isPresent()).isTrue();
        assertThat(duplicate.getSaksbehandler().get().asString()).isEqualTo("System");
        assertThat(duplicate.getStatus()).isEqualTo(Status.AVVIST);
    }


}
