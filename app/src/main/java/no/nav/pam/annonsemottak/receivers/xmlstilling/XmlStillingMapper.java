package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.markdown.HtmlToMarkdownConverter;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.stilling.Stilling;

class XmlStillingMapper {

    static Stilling fromDto(XmlStillingDto dto) {

        Stilling stilling = new Stilling(
                HtmlToMarkdownConverter.parse(dto.getTitle()).trim(),
                null,
                dto.getEmployer(),
                dto.getEmployerDescription(),
                dto.getJobDescription(),
                dto.getDueDate().toString(),
                Kilde.XML_STILLING.toString(),
                dto.getExternalUser(),
                null,
                dto.getExternalId()
        ).deactivate();

        stilling.setSystemModifiedDate(dto.getReceivedDate());

        return stilling;
    }

}
