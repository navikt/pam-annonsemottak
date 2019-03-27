package no.nav.pam.annonsemottak.receivers.xmlstilling;

import org.junit.Test;

import static no.nav.pam.annonsemottak.receivers.xmlstilling.XmlStillingMapper.STILLINGSTITTEL_ER_IKKE_SATT;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.XmlStillingMapper.fromDto;
import static org.assertj.core.api.Assertions.assertThat;

public class XmlStillingMapperTest {

    @Test
    public void at_stillingstittel_blir_ok() {
        assertThat(fromDto(dtoWithTitle("Tittelen")).getTitle()).isEqualTo("Tittelen");
    }

    @Test
    public void at_tom_stillingstittel_blir_erstattes_med_placeholder() {
        assertThat(fromDto(dtoWithTitle("")).getTitle()).isEqualTo(STILLINGSTITTEL_ER_IKKE_SATT);
    }

    private XmlStillingDto dtoWithTitle(String title) {
        XmlStillingDto dto = new XmlStillingDto();
        dto.setStillingstittel(title);
        return dto;
    }
}
