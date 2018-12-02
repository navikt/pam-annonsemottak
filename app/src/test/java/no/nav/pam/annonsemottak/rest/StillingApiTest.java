package no.nav.pam.annonsemottak.rest;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
@Transactional
@Ignore// disabled until we have discussed about testing against real databases.
public class StillingApiTest {

//    private static StillingApi stillingApi;
//    @Inject
//    private StillingRepository stillingRepository;
//    private ResponseEntity response;
//    private PaginatedPayload<AnnonsehodePayload> payload;
//
//    @Before
//    public void initialize() {
//        final Stilling s1 = stilling()
//                .arbeidsgiver("Employer One")
//                .arbeidssted("Location One")
//                .tittel("Title One")
//                .saksbehandler("Assigned to One")
//                .status(Status.valueOfStatuskode("1"))
//                .build();
//        stillingRepository.save(s1);
//        final Stilling s2 = stilling()
//                .arbeidsgiver("Employer Two")
//                .arbeidssted("Location Two")
//                .tittel("Title Two")
//                .saksbehandler("Assigned to Two")
//                .status(Status.valueOfStatuskode("2"))
//                .build();
//        stillingRepository.save(s2);
//        stillingApi = new StillingApi(stillingRepository);
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void getListOfAnnonsehode_should_filter_on_arbeidsgiver()
//            throws Exception {
//        // 1. None.
//        response = stillingApi.getListOfAnnonsehode("Unknown", null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(0, payload.getData().size());
//
//        // 2. One.
//        response = stillingApi.getListOfAnnonsehode("ONE", null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(1, payload.getData().size());
//        assertTrue(payload.getData().get(0).getArbeidsgiver().contains("One"));
//
//        // 3. All.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(2, payload.getData().size());
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void getListOfAnnonsehode_should_filter_on_arbeidssted() {
//        // 1. None.
//        response = stillingApi.getListOfAnnonsehode(null, "Unknown", null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(0, payload.getData().size());
//
//        // 2. One.
//        response = stillingApi.getListOfAnnonsehode(null, "ONE", null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(1, payload.getData().size());
//        assertTrue(payload.getData().get(0).getArbeidssted().contains("One"));
//
//        // 3. All.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(2, payload.getData().size());
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void getListOfAnnonsehode_should_filter_on_stillingstittel() {
//        // 1. None.
//        response = stillingApi.getListOfAnnonsehode(null, null, "Unknown", null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(0, payload.getData().size());
//
//        // 2. One.
//        response = stillingApi.getListOfAnnonsehode(null, null, "ONE", null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(1, payload.getData().size());
//        assertTrue(payload.getData().get(0).getTittel().contains("One"));
//
//        // 3. All.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(2, payload.getData().size());
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void getListOfAnnonsehode_should_filter_on_saksbehandler() {
//        // 1. None.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, "Unknown", null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(0, payload.getData().size());
//
//        // 2. One.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, "ONE", null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(1, payload.getData().size());
//        assertTrue(payload.getData().get(0).getSaksbehandler().equals("Assigned to One"));
//
//        // 3. All.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(2, payload.getData().size());
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void getListOfAnnonsehode_should_filter_on_status() {
//        // 1. None.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, "3", null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(0, payload.getData().size());
//
//        // 2. One.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, "1", null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(1, payload.getData().size());
//        assertTrue(payload.getData().get(0).getStatus().equals("1"));
//
//        // 3. All.
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, null, null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertEquals(2, payload.getData().size());
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void annonsehodeliste_skal_sorteres_paa_arbeidsgiver() {
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, "arbeidsgiver", null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertThat(payload.getData().get(0).getArbeidsgiver(), is(equalTo("Employer One")));
//        assertThat(payload.getData().get(1).getArbeidsgiver(), is(equalTo("Employer Two")));
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void annonsehodeliste_skal_sorteres_paa_status() {
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, Sortering.OrderBy.STATUS.name(), null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertThat(payload.getData().get(0).getStatus(), is(equalTo("2")));
//        // 2 tilsvarer Godkjent, og 1 under arbeid, så 2 kommer først
//        assertThat(payload.getData().get(1).getStatus(), is(equalTo("1")));
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void annonsehodeliste_skal_sorteres_paa_mottattdato() {
//        ResponseEntity annonsehodeList = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, Sortering.OrderBy.MOTTATTDATO.name(), null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) annonsehodeList.getBody();
//        assertThat(payload.getData().get(0).getArbeidsgiver(), is(equalTo("Employer One")));
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void annonsehodeliste_skal_sorteres_paa_arbeidssted() {
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, Sortering.OrderBy.STED.name(), null);
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertThat(payload.getData().get(0).getArbeidssted(), is(equalTo("Location One")));
//        assertThat(payload.getData().get(1).getArbeidssted(), is(equalTo("Location Two")));
//    }
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void annonsehodeliste_skal_sorteres_paa_saksbehandler() {
//        response = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, Sortering.OrderBy.SAKSBEHANDLER.name(), "desc");
//        payload = (PaginatedPayload<AnnonsehodePayload>) response.getBody();
//        assertThat(payload.getData().get(0).getSaksbehandler(), is(equalTo("Assigned to Two")));
//        assertThat(payload.getData().get(1).getSaksbehandler(), is(equalTo("Assigned to One")));
//    }
//
//
//    @Test
//    @SuppressWarnings("unchecked")
//    public void annonsehodeliste_skal_sorteres_paa_stillingstittel() {
//        ResponseEntity annonsehodeList = stillingApi.getListOfAnnonsehode(null, null, null, null, null, null, null, Sortering.OrderBy.TITTEL.name(), "desc");
//        payload = (PaginatedPayload<AnnonsehodePayload>) annonsehodeList.getBody();
//        assertThat(payload.getData().get(0).getTittel(), is(equalTo("Title Two")));
//        assertThat(payload.getData().get(1).getTittel(), is(equalTo("Title One")));
//    }
}