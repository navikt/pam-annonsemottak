package no.nav.pam.annonsemottak.receivers.dexi;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import no.nav.pam.annonsemottak.receivers.common.PropertyNames;
import no.nav.pam.annonsemottak.stilling.Stilling;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class DexiModelTest {

    private static Level original;

    @BeforeAll
    public static void beforeClass() {
        Logger logger = ((Logger) LoggerFactory.getLogger(DexiModel.class));
        original = logger.getLevel();
        logger.setLevel(Level.ERROR);
    }

    @AfterAll
    public static void afterClass() {
        ((Logger) LoggerFactory.getLogger(DexiModel.class)).setLevel(original);
    }

    @Test
    public void mapToNullIfEmptyAnnonsetittel() {

        Map<String, String> map = new HashMap<>();
        map.put(DexiModel.EXTERNALID, "ID");
        map.put(DexiModel.ANNONSEURL, "http://www.nav.no");
        map.put(DexiModel.ANNONSETEKST, "Some non-empty text");
        map.put(DexiModel.ARBEIDSGIVER, "Arbeidsgiver");

        assertNull(DexiModel.toStilling(map, "WallE"));

        map.put(DexiModel.ANNONSETITTEL, "");
        assertNull(DexiModel.toStilling(map, "WallE"));

        map.put(DexiModel.ANNONSETITTEL, "Some non-empty text");
        assertNotNull(DexiModel.toStilling(map, "WallE"));

    }

    @Test
    public void mapToNullIfEmptyAnnonsetekst() {

        Map<String, String> map = new HashMap<>();
        map.put(DexiModel.EXTERNALID, "ID");
        map.put(DexiModel.ANNONSEURL, "http://www.nav.no");
        map.put(DexiModel.ANNONSETITTEL, "Some non-empty text");
        map.put(DexiModel.ARBEIDSGIVER, "Arbeidsgiver");

        assertNull(DexiModel.toStilling(map, "WallE"));

        map.put(DexiModel.ANNONSETEKST, "");
        assertNull(DexiModel.toStilling(map, "WallE"));

        map.put(DexiModel.ANNONSETEKST, "Some non-empty text");
        assertNotNull(DexiModel.toStilling(map, "WallE"));

    }

    @Test
    public void testConcatenationMedNullIngress() {

        String annonsetekst = "Eksempeltekst";

        Map<String, String> map = new HashMap<>();
        map.put(DexiModel.ANNONSETITTEL, "Some non-empty title to get a valid Stilling");
        map.put(DexiModel.INGRESS, null);
        map.put(DexiModel.ANNONSETEKST, annonsetekst);
        map.put(DexiModel.ARBEIDSGIVER, "Arbeidsgiver");

        Stilling stilling = DexiModel.toStilling(map, "testRobot");
        assertNotNull(stilling);
        assertEquals(stilling.getJobDescription(), annonsetekst + "\n");

    }

    @Test
    public void testConcatenationMedIngress() {

        String ingress = "Eksempelingress";
        String annonsetekst = "Eksempeltekst";

        Map<String, String> map = new HashMap<>();
        map.put(DexiModel.ANNONSETITTEL, "Some non-empty title to get a valid Stilling");
        map.put(DexiModel.INGRESS, ingress);
        map.put(DexiModel.ANNONSETEKST, annonsetekst);
        map.put(DexiModel.ARBEIDSGIVER, "Arbeidsgiver");

        Stilling stilling = DexiModel.toStilling(map, "testRobot");
        assertNotNull(stilling);
        assertEquals(stilling.getJobDescription(), ingress.concat("\n" + annonsetekst + "\n"));

    }

    @Test
    public void mapPositionCount() {
        Map<String, String> map = new HashMap<>();
        map.put(DexiModel.EXTERNALID, "ID");
        map.put(DexiModel.ANNONSEURL, "http://www.nav.no");
        map.put(DexiModel.ANNONSETEKST, "Some non-empty text");
        map.put(DexiModel.ARBEIDSGIVER, "Arbeidsgiver");
        map.put(DexiModel.ANNONSETITTEL, "Some nice text");
        assertEquals("1", DexiModel.toStilling(map, "WallE").getProperties().get(PropertyNames.ANTALL_STILLINGER));

        map.put(DexiModel.ANTALL_STILLINGER, "2");
        assertEquals("2", DexiModel.toStilling(map, "WallE").getProperties().get(PropertyNames.ANTALL_STILLINGER));
    }
}
