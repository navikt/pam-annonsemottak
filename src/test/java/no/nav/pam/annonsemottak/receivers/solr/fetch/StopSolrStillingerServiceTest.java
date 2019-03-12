package no.nav.pam.annonsemottak.receivers.solr.fetch;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.finn.unleash.FakeUnleash;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.stilling.AnnonseStatus;
import no.nav.pam.annonsemottak.stilling.Stilling;
import no.nav.pam.annonsemottak.stilling.StillingRepository;
import no.nav.pam.unleash.UnleashProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static no.nav.pam.annonsemottak.receivers.solr.fetch.SolrFetchService.fraArbeidsgiver;
import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class StopSolrStillingerServiceTest {

    private MeterRegistry meterRegistry = new SimpleMeterRegistry();  // cannot mock due to weakness in mockito when mocking overloadad vararg methods;

    private StillingRepository stillingRepository = mock(StillingRepository.class);

    private StopSolrStillingerService service;

    @Captor
    private ArgumentCaptor<List<Stilling>> captor;

    private FakeUnleash fakeUnleash = new FakeUnleash();

    @Before
    public void setUp() {
        fakeUnleash.disableAll();
        UnleashProvider.initialize(fakeUnleash);
        MockitoAnnotations.initMocks(this);
        service = new StopSolrStillingerService(stillingRepository, meterRegistry);
    }

    @Test
    public void that_db_ads_not_fetched_from_solr_is_stopped() {
        List<Stilling> allFetchedAds = Arrays.asList(
                enkelStilling().build(),
                enkelStilling().build());

        List<Stilling> activeDbAds = Arrays.asList(
                enkelStillingMedUuid(allFetchedAds.get(0).getUuid()),
                enkelStillingMedUuid(allFetchedAds.get(1).getUuid()),
                enkelStillingMedUuid(UUID.randomUUID().toString()));

        when(stillingRepository.findByKildeAndAnnonseStatus(eq(Kilde.STILLINGSOLR.value()), eq(AnnonseStatus.AKTIV)))
                .thenReturn(activeDbAds);

        service.findAndStopOldSolrStillinger(allFetchedAds);

        verify(stillingRepository).saveAll(captor.capture());

        List<Stilling> stoppedAds = captor.getValue();
        assertThat(stoppedAds).hasSize(1);
        assertThat(stoppedAds.get(0).getAnnonseStatus()).isEqualTo(AnnonseStatus.STOPPET);

    }

    @Test
    public void that_db_ads_fetched_from_solr_is_not_stopped() {
        List<Stilling> allFetchedAds = Arrays.asList(
                enkelStilling().build(),
                enkelStilling().build(),
                enkelStilling().build());

        List<Stilling> activeDbAds = Arrays.asList(
                enkelStillingMedUuid(allFetchedAds.get(0).getUuid()),
                enkelStillingMedUuid(allFetchedAds.get(1).getUuid()));

        when(stillingRepository.findByKildeAndAnnonseStatus(eq(Kilde.STILLINGSOLR.value()), eq(AnnonseStatus.AKTIV)))
                .thenReturn(activeDbAds);

        service.findAndStopOldSolrStillinger(allFetchedAds);

        verify(stillingRepository).saveAll(captor.capture());

        List<Stilling> stoppedAds = captor.getValue();
        assertThat(stoppedAds).hasSize(0);

    }

    @Test
    public void that_db_xml_stilling_ads_fetched_from_solr_is_not_stopped() {
        fakeUnleash.enableAll();
        List<Stilling> allFetchedAds = Arrays.asList(
                enkelStilling().build(),
                enkelStilling().build());

        List<Stilling> activeDbAds = Arrays.asList(
                enkelStillingMedUuid(allFetchedAds.get(0).getUuid()),
                enkelStillingMedUuid(allFetchedAds.get(1).getUuid()),
                enkelStillingMedUuid(UUID.randomUUID().toString()),
                xmlStillingAdMedUuid(UUID.randomUUID().toString()));

        when(stillingRepository.findByKildeAndAnnonseStatus(eq(Kilde.STILLINGSOLR.value()), eq(AnnonseStatus.AKTIV)))
                .thenReturn(activeDbAds);

        service.findAndStopOldSolrStillinger(allFetchedAds);

        verify(stillingRepository).saveAll(captor.capture());

        List<Stilling> stoppedAds = captor.getValue();
        assertThat(stoppedAds).hasSize(1);
        assertThat(stoppedAds.get(0).getAnnonseStatus()).isEqualTo(AnnonseStatus.STOPPET);

    }

    @Test
    public void that_feature_toggle_for_xml_stilling_do_not_interfer_when_disabled() {
        fakeUnleash.disableAll();
        List<Stilling> allFetchedAds = Arrays.asList(
                enkelStilling().build(),
                enkelStilling().build());

        List<Stilling> activeDbAds = Arrays.asList(
                enkelStillingMedUuid(allFetchedAds.get(0).getUuid()),
                enkelStillingMedUuid(allFetchedAds.get(1).getUuid()),
                enkelStillingMedUuid(UUID.randomUUID().toString()),
                xmlStillingAdMedUuid(UUID.randomUUID().toString()));

        when(stillingRepository.findByKildeAndAnnonseStatus(eq(Kilde.STILLINGSOLR.value()), eq(AnnonseStatus.AKTIV)))
                .thenReturn(activeDbAds);

        service.findAndStopOldSolrStillinger(allFetchedAds);

        verify(stillingRepository).saveAll(captor.capture());

        List<Stilling> stoppedAds = captor.getValue();
        assertThat(stoppedAds).hasSize(2);
        assertThat(stoppedAds.get(0).getAnnonseStatus()).isEqualTo(AnnonseStatus.STOPPET);

    }


    private Stilling enkelStillingMedUuid(String uuid) {
        Stilling stilling = enkelStilling().build();
        stilling.setUuid(uuid);
        return stilling;
    }

    private Stilling xmlStillingAdMedUuid(String uuid) {
        Stilling stilling = enkelStilling().medium(fraArbeidsgiver).build();
        stilling.setUuid(uuid);
        return stilling;
    }
}
