package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static no.nav.pam.annonsemottak.receivers.Kilde.XML_STILLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class XmlStillingExternalRunTest {

    private XmlStillingExternalRun externalRun;

    private ExternalRunService externalRunService = mock(ExternalRunService.class);

    @Before
    public void setUp() {
        externalRun = new XmlStillingExternalRun(externalRunService);
    }

    @Test
    public void that_existing_external_run_is_fetched_for_xml_stilling() {
        LocalDateTime existingDate = LocalDateTime.of(2019, 1, 11, 14, 51, 11);
        ExternalRun existing = new ExternalRun(100L, XML_STILLING.toString(), XML_STILLING.value(), existingDate);
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(existing);

        LocalDateTime fetchedDate = externalRun.lastRunSupplier().get();

        assertThat(fetchedDate).isEqualTo(existingDate);
        verify(externalRunService).findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value()));
    }

    @Test
    public void that_default_date_is_returned_when_no_existing_external_run_is_present() {
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(null);

        LocalDateTime fetchedDate = externalRun.lastRunSupplier().get();

        assertThat(fetchedDate).isEqualTo(XmlStillingExternalRun.DEFAULT_DATE);
        verify(externalRunService).findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value()));
    }

    @Test
    public void that_update_latest_uses_existing_id() {
        ExternalRun existing = new ExternalRun(42L, XML_STILLING.toString(), XML_STILLING.value(), now());
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(existing);

        externalRun.updateLatest(now());

        ArgumentCaptor<ExternalRun> captor = ArgumentCaptor.forClass(ExternalRun.class);
        verify(externalRunService).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(42L);
    }

    @Test
    public void that_update_latest_uses_no_id_when_no_id_exists() {
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(null);

        externalRun.updateLatest(now());

        ArgumentCaptor<ExternalRun> captor = ArgumentCaptor.forClass(ExternalRun.class);
        verify(externalRunService).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

}
