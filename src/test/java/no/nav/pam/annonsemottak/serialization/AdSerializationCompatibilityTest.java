package no.nav.pam.annonsemottak.serialization;

import no.nav.pam.annonsemottak.app.config.AppConfig;
import no.nav.pam.annonsemottak.rest.dto.StillingDTO;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingBuilder;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AdSerializationCompatibilityTest {

    private static final LocalDateTime PUBLISHED = LocalDateTime.of(2021, 3, 10, 8, 0, 0);
    private static final LocalDateTime EXPIRES = LocalDateTime.of(2021, 9, 1, 12, 30, 45);
    private static final LocalDateTime CREATED = LocalDateTime.of(2021, 3, 15, 10, 20, 30);
    private static final LocalDateTime UPDATED = LocalDateTime.of(2021, 3, 16, 11, 22, 33);
    private static final LocalDateTime SYSTEM_MODIFIED = LocalDateTime.of(2021, 3, 17, 12, 24, 36);

    private Stilling fixedStilling() {
        var stilling = new StillingBuilder()
                .uuid("11111111-2222-3333-4444-555555555555")
                .title("Stillingstittel")
                .place("Oslo")
                .employer("Arbeidsgiver AS")
                .employerDescription("Arbeidsgiverbeskrivelse")
                .jobDescription("Stillingstekst")
                .dueDate("01-09-2021")
                .kilde("KILDE")
                .medium("MEDIUM")
                .url("https://example.test/stilling/1")
                .externalId("ext-1")
                .withProperties(Map.of("antallStillinger", "3"))
                .expires(EXPIRES)
                .published(PUBLISHED)
                .systemModifiedDate(SYSTEM_MODIFIED)
                .build();

        stilling.setCreated(CREATED);
        stilling.setUpdated(UPDATED);
        stilling.setCreatedBy("test1234");
        stilling.setUpdatedBy("test1234");
        stilling.setCreatedByDisplayName("Testuser Displayname");
        stilling.setUpdatedByDisplayName("Testuser Displayname");
        stilling.setId(42L);
        return stilling;
    }

    private StillingDTO fixedStillingDto() {
        var dto = new StillingDTO();
        dto.setUuid("uuid-1");
        dto.setEmployerName("Arbeidsgiver AS");
        dto.setJobTitle("Tittel");
        dto.setJobLocation("Oslo");
        dto.setJobDescription("Tekst");
        dto.setApplicationDeadline("snarest");
        dto.setKilde("KILDE");
        dto.setMedium("MEDIUM");
        dto.setOrgNummer("123456789");
        dto.setAntallStillinger(2);
        dto.setPubliserFra(PUBLISHED);
        dto.setSistePubliseringsDato(EXPIRES);
        dto.setStatus("MOTTATT");
        return dto;
    }

    private String readGolden(String name) throws Exception {
        return Files.readString(Path.of("src/test/resources/serialization", name), StandardCharsets.UTF_8).trim();
    }

    @Test
    void stilling_serializes_to_json() throws Exception {
        var mapper = new AppConfig().jacksonMapper();

        var actual = mapper.writeValueAsString(fixedStilling());

        // Explicit, human-readable date-format expectations (this is the main upgrade risk).
        assertThat(actual)
                .contains("\"created\":\"2021-03-15T10:20:30\"")
                .contains("\"updated\":\"2021-03-16T11:22:33\"")
                .contains("\"published\":\"2021-03-10T08:00:00\"")
                .contains("\"expires\":\"2021-09-01T12:30:45\"")
                .contains("\"systemModifiedDate\":\"2021-03-17T12:24:36\"");

        // Full output contract, compared as a tree so JSON key ordering differences are tolerated
        // but any value/shape change (including date format) fails.
        var expectedTree = mapper.readTree(readGolden("expected-stilling.json"));
        var actualTree = mapper.readTree(actual);
        assertThat(actualTree).isEqualTo(expectedTree);
    }

    @Test
    void stillingDto_serializes_to_json() throws Exception {
        var mapper = new AppConfig().jacksonMapper();

        var actual = mapper.writeValueAsString(fixedStillingDto());

        assertThat(actual)
                .contains("\"publiserFra\":\"10-03-2021 08:00:00\"")
                .contains("\"sistePubliseringsDato\":\"01-09-2021 12:30:45\"");

        var expectedTree = mapper.readTree(readGolden("expected-stillingdto.json"));
        var actualTree = mapper.readTree(actual);
        assertThat(actualTree).isEqualTo(expectedTree);
    }

    @Test
    void stillingDto_deserializes_dates_json() throws Exception {
        var mapper = new AppConfig().jacksonMapper();

        var dto = mapper.readValue(readGolden("expected-stillingdto.json"), StillingDTO.class);

        assertThat(dto.getUuid()).isEqualTo("uuid-1");
        assertThat(dto.getEmployerName()).isEqualTo("Arbeidsgiver AS");
        assertThat(dto.getJobTitle()).isEqualTo("Tittel");
        assertThat(dto.getJobLocation()).isEqualTo("Oslo");
        assertThat(dto.getApplicationDeadline()).isEqualTo("snarest");
        assertThat(dto.getOrgNummer()).isEqualTo("123456789");
        assertThat(dto.getAntallStillinger()).isEqualTo(2);
        assertThat(dto.getPubliserFra()).isEqualTo(PUBLISHED);
        assertThat(dto.getSistePubliseringsDato()).isEqualTo(EXPIRES);
        assertThat(dto.getStatus()).isEqualTo("MOTTATT");
    }

    @Test
    void stillingDto_date_roundtrip_is_lossless() throws Exception {
        var mapper = new AppConfig().jacksonMapper();

        var json = mapper.writeValueAsString(fixedStillingDto());
        var roundTripped = mapper.readValue(json, StillingDTO.class);

        assertThat(roundTripped.getPubliserFra()).isEqualTo(PUBLISHED);
        assertThat(roundTripped.getSistePubliseringsDato()).isEqualTo(EXPIRES);
    }
}
