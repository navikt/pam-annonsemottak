package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static java.util.Optional.empty;
import static no.nav.pam.annonsemottak.receivers.Kilde.STILLINGSOLR;
import static no.nav.pam.annonsemottak.receivers.Kilde.XML_STILLING;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
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
    private Function<Stilling, Gruppe> size_2_4_1_arena_groups = new Function<Stilling, Gruppe>() {
        int i = 0;
        @Override
        public Gruppe apply(Stilling stilling) {
            return new Gruppe[]{ CHANGED, CHANGED, NEW, NEW, NEW, NEW, CHANGED_ARENA }[i++];
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
    public void update_arena_stillinger() {
        List<Stilling> nyeStillinger = listOf6stillingerAnd1ArenaStillingLast();
        Stillinger stillinger = facade.updateStillinger(nyeStillinger, size_2_4_1_arena_groups);

        verify(repository, times(3)).findByKildeAndMediumAndExternalId(any(), any(), any());

        verify(repository, times(7)).save(captor.capture());
        assertThat(captor.getAllValues()).hasSize(7); // changed + new

        assertThat(captor.getAllValues().get(6).getKilde()).isEqualTo(STILLINGSOLR.value());
        assertThat(captor.getAllValues().get(6).getMedium()).isEqualTo("Overført fra arbeidsgiver");
        assertThat(captor.getAllValues().get(6).getExternalId()).isEqualTo("11");

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




    @Test
    public void test_that_update_all_strategy_ignores_arenaId_when_not_found_in_database() {

        when(repository.findByKildeAndMediumAndExternalId(eq(STILLINGSOLR.value()), eq("Overført fra arbeidsgiver"), any()))
                .thenReturn(empty());
        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(empty());

        assertThat(facade.saveAllGroupingStrategy(enkelStillingMedArenaId("11"))).isEqualTo(NEW);

    }
    @Test
    public void test_that_update_changed_and_new_strategy_ignores_arenaId_when_not_found_in_database() {

        when(repository.findByKildeAndMediumAndExternalId(eq(STILLINGSOLR.value()), eq("Overført fra arbeidsgiver"), any()))
                .thenReturn(empty());
        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(empty());

        assertThat(facade.saveOnlyNewAndChangedGroupingStrategy(enkelStillingMedArenaId("11"))).isEqualTo(NEW);

    }

    @Test
    public void test_that_update_all_strategy_groups_as_arena_change() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(arenaDbStilling("11")));

        assertThat(facade.saveAllGroupingStrategy(enkelStillingMedArenaId("11"))).isEqualTo(CHANGED_ARENA);

        verify(repository).findByKildeAndMediumAndExternalId(eq(STILLINGSOLR.value()), eq("Overført fra arbeidsgiver"), eq("11"));
    }

    @Test
    public void test_that_update_changed_and_new_strategy_as_arena_change() {

        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(arenaDbStilling("11")));

        assertThat(facade.saveOnlyNewAndChangedGroupingStrategy(enkelStillingMedArenaId("11"))).isEqualTo(CHANGED_ARENA);

        verify(repository).findByKildeAndMediumAndExternalId(eq(STILLINGSOLR.value()), eq("Overført fra arbeidsgiver"), eq("11"));

    }

    @Test
    public void that_update_also_stops_ads_automatically_when_expiry_is_changed_to_the_past() {

        Stilling eksisterende = StillingTestdataBuilder.enkelStilling().kilde(XML_STILLING.toString()).utløpsdato("13.12.2018").systemModifiedDate(now().minusDays(1)).build();
        when(repository.findByKildeAndMediumAndExternalId(anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(eksisterende));

        Stilling oppdatering = StillingTestdataBuilder.enkelStilling().kilde(XML_STILLING.toString()).utløpsdato("12.12.2018").systemModifiedDate(now()).build();
        facade.updateStillinger(Arrays.asList(oppdatering), facade::saveOnlyNewAndChangedGroupingStrategy);

        ArgumentCaptor<Stilling> captor = ArgumentCaptor.forClass(Stilling.class);
        verify(repository).save(captor.capture());

        assertThat(captor.getValue().getAnnonseStatus()).isEqualTo(AnnonseStatus.STOPPET);

    }

    private List<Stilling> listOf7stillinger() {
        return IntStream.range(0, 7)
                .mapToObj(i -> StillingTestdataBuilder.enkelStilling()
                        .externalId(String.valueOf(i))
                        .systemModifiedDate(now()).build())
                .collect(Collectors.toList());
    }

    private List<Stilling> listOf6stillingerAnd1ArenaStillingLast() {
        List<Stilling> l = IntStream.range(0, 6)
                .mapToObj(i -> StillingTestdataBuilder.enkelStilling()
                        .externalId(String.valueOf(i))
                        .systemModifiedDate(now()).build())
                .collect(Collectors.toList());

        l.add(StillingTestdataBuilder.enkelStilling()
                .kilde(STILLINGSOLR.value())
                .medium("Overført fra arbeidsgiver")
                .externalId("11")
                .systemModifiedDate(now())
                .build());
        return l;
    }

    private Stilling enkelStilling() {
        return StillingTestdataBuilder.enkelStilling().build();
    }

    private Stilling enkelStilling(String tekst) {
        return StillingTestdataBuilder.enkelStilling()
                .stillingstekst(tekst)
                .build();
    }

    private Stilling enkelStillingMedArenaId(String arenaId) {
        Stilling stilling = StillingTestdataBuilder.enkelStilling().build();
        stilling.setArenaId(arenaId);
        return stilling;
    }
    private Stilling arenaDbStilling(String arenaId) {
        return StillingTestdataBuilder.enkelStilling()
                .externalId(arenaId).build();
    }

}
