package no.nav.pam.annonsemottak.annonsemottak.solr.fetch;

import no.nav.pam.annonsemottak.annonsemottak.common.PropertyNames;
import no.nav.pam.annonsemottak.annonsemottak.solr.StillingSolrBean;
import no.nav.pam.annonsemottak.stilling.Status;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class StillingSolrBeanMapperTest {

    @Test
    public void should_map_correctly_solrbean_to_stilling() {
        StillingSolrBean solrBean = createNewSolrBean();

        Stilling stilling = StillingSolrBeanMapper.mapToStilling(solrBean);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(stilling.getStillingstittel()).isEqualTo(solrBean.getTittel());
        softly.assertThat(stilling.getArbeidssted())
                .isEqualTo(solrBean.getGeografiskomrade().get(0));

        softly.assertThat(stilling.getArbeidsgiver().get().asString())
                .isEqualTo(solrBean.getArbeidsgivernavn());

        softly.assertThat(stilling.getArbeidsgiveromtale())
                .contains(solrBean.getBedriftspresentasjon());
        softly.assertThat(stilling.getAnnonsetekst())
                .contains(solrBean.getStillingsbeskrivelse());
        softly.assertThat(stilling.getExpires().minusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .isEqualTo(solrBean.getSoknadsfrist().getTime());
        softly.assertThat(stilling.getKilde()).isEqualTo("stillingsolr");
        softly.assertThat(stilling.getMedium()).isEqualTo(solrBean.getKildetekst());
        softly.assertThat(stilling.getExternalId()).isEqualTo(solrBean.getId().toString());
        Map<String, String> props = stilling.getProperties();
        softly.assertThat(props.get(PropertyNames.ANTALL_STILLINGER)).isEqualTo(solrBean.getAntallStillinger().toString());
        softly.assertThat(props.get(PropertyNames.KONTAKTPERSON)).isEqualTo(solrBean.getKontaktperson());
        softly.assertThat(props.get(PropertyNames.STILLINGSTITTEL)).isEqualTo(solrBean.getStillingstype());

        softly.assertAll();
    }

    @Test
    public void should_set_correct_reportee_status() {
        StillingSolrBean solrBean = createNewSolrBean();
        Stilling stilling = StillingSolrBeanMapper.mapToStilling(solrBean);
        SoftAssertions softIce = new SoftAssertions();
        softIce.assertThat(stilling.getStatus()).isEqualTo(Status.GODKJENT);
        softIce.assertThat(stilling.getSaksbehandler().isPresent()).isTrue();
        softIce.assertThat(stilling.getSaksbehandler().get().asString()).isEqualTo("System");
        softIce.assertAll();
    }

    private StillingSolrBean createNewSolrBean() {
        StillingSolrBean solrBean = new StillingSolrBean();

        solrBean.setTittel("Dette er en tittel");
        solrBean.setGeografiskomrade(new ArrayList<>(Collections.singletonList("Oslo")));
        solrBean.setKildetekst("Direktemeldt");
        solrBean.setId(1);
        solrBean.setArbeidsgivernavn("Arbeidsgivern AS");
        solrBean.setStillingsbeskrivelse("Beskrivelse av stillingen");
        solrBean.setBedriftspresentasjon("Dette er en presentasjon av bedriften.");
        solrBean.setPubliseresFra(new Date());
        solrBean.setSistePubliseringsdato(new Date());
        solrBean.setSoknadsfrist(new Date());

        solrBean.setAntallStillinger(3);
        solrBean.setKontaktperson("Navn Navnsdottir");
        solrBean.setStillingstype("Arbeider");

        return solrBean;
    }
}
