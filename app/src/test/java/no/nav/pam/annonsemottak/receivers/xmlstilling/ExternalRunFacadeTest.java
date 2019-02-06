package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRun;
import no.nav.pam.annonsemottak.receivers.externalRun.ExternalRunService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import static java.time.LocalDateTime.now;
import static no.nav.pam.annonsemottak.receivers.Kilde.XML_STILLING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExternalRunFacadeTest {

    private static final LocalDateTime JAN_28_2019 = LocalDateTime.of(2019, 1, 28, 18, 4, 5);
    private ExternalRunFacade externalRun;

    @Mock
    private ExternalRunService externalRunService;

    @Mock
    private Function<LocalDateTime, Stillinger> saveMethodMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        externalRun = new ExternalRunFacade(externalRunService);
    }

    @Test
    public void that_existing_external_run_date_is_passed_to_decorated_function() {

        ExternalRun existing = new ExternalRun(100L, XML_STILLING.toString(), XML_STILLING.value(), JAN_28_2019.minusDays(2));
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(existing);
        when(saveMethodMock.apply(any())).thenReturn(new StillingerStub(JAN_28_2019));

        externalRun.decorate(saveMethodMock);

        verify(saveMethodMock).apply(eq(JAN_28_2019.minusDays(2)));
    }

    @Test
    public void that_default_date_is_used_when_no_existing_external_run_is_present() {

        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(null);
        when(saveMethodMock.apply(any())).thenReturn(new StillingerStub(JAN_28_2019));

        externalRun.decorate(saveMethodMock);

        verify(saveMethodMock).apply(eq(ExternalRunFacade.DEFAULT_DATE));
    }

    @Test
    public void that_update_latest_uses_existing_id() {
        ExternalRun existing = new ExternalRun(42L, XML_STILLING.toString(), XML_STILLING.value(), now());
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(existing);
        when(saveMethodMock.apply(any())).thenReturn(new StillingerStub(JAN_28_2019));

        externalRun.decorate(saveMethodMock);

        ArgumentCaptor<ExternalRun> captor = ArgumentCaptor.forClass(ExternalRun.class);
        verify(externalRunService).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(42L);
    }

    @Test
    public void that_update_latest_uses_no_id_when_no_id_exists() {
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(null);
        when(saveMethodMock.apply(any())).thenReturn(new StillingerStub(JAN_28_2019));

        externalRun.decorate(saveMethodMock);

        ArgumentCaptor<ExternalRun> captor = ArgumentCaptor.forClass(ExternalRun.class);
        verify(externalRunService).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
    }

    @Test
    public void that_update_latest_saves_new_date() {
        when(externalRunService.findByNameAndMedium(eq(XML_STILLING.toString()), eq(XML_STILLING.value())))
                .thenReturn(null);
        when(saveMethodMock.apply(any())).thenReturn(new StillingerStub(JAN_28_2019));

        externalRun.decorate(saveMethodMock);

        ArgumentCaptor<ExternalRun> captor = ArgumentCaptor.forClass(ExternalRun.class);
        verify(externalRunService).save(captor.capture());
        assertThat(captor.getValue().getLastRun()).isEqualTo(JAN_28_2019);
    }


    private class StillingerStub extends Stillinger {
        private final LocalDateTime date;

        StillingerStub(LocalDateTime date) {
            super(Collections.emptyList(), s -> Stillinger.Gruppe.NEW);
            this.date = date;
        }

        @Override
        Optional<LocalDateTime> latestDate() {
            return Optional.of(JAN_28_2019);
        }
    }

}
