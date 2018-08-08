package no.nav.pam.annonsemottak.annonsemottak.amedia;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AmediaRequestParametereTest {

    @Test
    public void kanOppretteEnParamterStrengMedAlleVerdier() {
        DateTime sistModifisert = new DateTime(2017, 10, 20, 11, 0, 0, DateTimeZone.UTC);
        AmediaRequestParametere parametere = new AmediaRequestParametere(sistModifisert, true, 200);

        assertThat(parametere.asString()).isEqualTo(
            "?q=%20%2Btransaction_type:11%20%2Bsystem_modified_time:>2017-10-20T10%5C:50%5C:00Z&sort=system_modified_time:asc&size=200&_source=true");
    }

    @Test
    public void modifisertDatoErNull() {
        AmediaRequestParametere parametere = new AmediaRequestParametere(null, true, 200);

        assertThat(parametere.asString()).isEqualTo(
            "?q=%20%2Btransaction_type:11%20%2Bsystem_modified_time:>1900-01-01T00%5C:00%5C:00Z&sort=system_modified_time:asc&size=200&_source=true");
    }


    @Test
    public void utenInhhold() {
        DateTime sistModifisert = new DateTime(2017, 10, 20, 11, 0, 0, DateTimeZone.UTC);
        AmediaRequestParametere parametere = new AmediaRequestParametere(sistModifisert, false,
            200);

        assertThat(parametere.asString()).isEqualTo(
            "?q=%20%2Btransaction_type:11%20%2Bsystem_modified_time:>2017-10-20T10%5C:50%5C:00Z&sort=system_modified_time:asc&size=200&_source=false");
    }
}