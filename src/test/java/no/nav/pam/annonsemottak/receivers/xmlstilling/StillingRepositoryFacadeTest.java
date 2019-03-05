package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class StillingRepositoryFacadeTest {

    private Function<Stilling, Gruppe> size_2_4_1_groups = new Function<Stilling, Gruppe>() {
        int i = 0;
        @Override
        public Gruppe apply(Stilling stilling) {
            return new Gruppe[]{ CHANGED, CHANGED, NEW, NEW, NEW, NEW, UNCHANGED }[i++];
        }
    };

    private StillingRepositoryFacade facade;

    @Captor
    private ArgumentCaptor<Stilling> captor;

    @Mock
    private StillingRepository repository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        facade = new StillingRepositoryFacade(repository);
    }

    @Test
    public void update_stillinger() {
        Stillinger stillinger = facade.updateStillinger(listOf7stillinger(), size_2_4_1_groups);

        verify(repository, times(2)).findByKildeAndMediumAndExternalId(any(), any(), any());

        verify(repository, times(6)).save(captor.capture());
        assertThat(captor.getAllValues()).hasSize(6); // changed + new

        assertThat(stillinger.asList()).hasSize(7);
    }

    @Test
    public void test_that_update_all_strategy_groups_new_correctly() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(enkelStilling()));

        assertThat(facade.saveAllGroupingStrategy(enkelStilling())).isEqualTo(CHANGED);
    }

    @Test
    public void test_that_update_all_strategy_groups_existing_correctly() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(empty());

        assertThat(facade.saveAllGroupingStrategy(enkelStilling())).isEqualTo(NEW);

    }

    @Test
    public void test_that_update_changed_and_new_strategy_groups_new_correctly() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(empty());

        assertThat(facade.saveOnlyNewAndChangedGroupingStrategy(enkelStilling())).isEqualTo(NEW);
    }

    @Test
    public void test_that_update_changed_and_new_strategy_groups_existing_with_hash_mismatch_correctly() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(enkelStilling("tekst db")));

        assertThat(facade.saveOnlyNewAndChangedGroupingStrategy(enkelStilling("tekst ny"))).isEqualTo(CHANGED);
    }

    @Test
    public void test_that_update_changed_and_new_strategy_groups_existing_with_annonse_status_flag_not_active_correctly() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(enkelStilling("tekst").deactivate()));

        assertThat(facade.saveOnlyNewAndChangedGroupingStrategy(enkelStilling("tekst"))).isEqualTo(CHANGED);
    }

    @Test
    public void test_that_update_changed_and_new_strategy_groups_existing_same_hash_and_status_active_correctly() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(enkelStilling("tekst same")));

        assertThat(facade.saveOnlyNewAndChangedGroupingStrategy(enkelStilling("tekst same"))).isEqualTo(UNCHANGED);
    }

    private List<Stilling> listOf7stillinger() {
        return IntStream.range(0, 7)
                .mapToObj(i -> StillingTestdataBuilder.enkelStilling()
                        .externalId(String.valueOf(i))
                        .systemModifiedDate(now()).build())
                .collect(Collectors.toList());
    }

    private Stilling enkelStilling() {
        return StillingTestdataBuilder.enkelStilling().build();
    }

    private Stilling enkelStilling(String tekst) {
        return StillingTestdataBuilder.enkelStilling()
                .stillingstekst(tekst)
                .build();
    }

}
