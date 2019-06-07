package no.nav.pam.annonsemottak.receivers.amedia;

import com.google.common.collect.Lists;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class AmediaRequestParametere {

    private static final int MODIFISERT_DATO_BUFFER_MINUTT = 10;
    private static final Logger LOG = LoggerFactory.getLogger(AmediaRequestParametere.class);
    private final static String DEFAULT_TRANSACTION_TYPE = "11"; // Stillinger
    private final static String DEFAULT_SORTERING = "system_modified_time:asc";
    private final static String AMEDIA_REQUEST_PARAMETERS =
        " +transaction_type:${transactionType} +system_modified_time:>${sisteModifiedDate}&sort=${sortering}&size=${maxAntallTreff}&_source=${medInnhold}";

    // Alle feltene her som er String blir automagisk input til query via reflection som trigges når vi kaller asString på klassen...
    // De må være lik nøkkel i templatestreng.
    private final String sisteModifiedDate;
    private final String transactionType;
    private final String sortering;
    private final String maxAntallTreff;
    private final String medInnhold;


    private static final LocalDateTime DAWN_OF_TIME_LOCALDATETIME = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    static final AmediaRequestParametere DAWN_OF_TIME = new AmediaRequestParametere(DAWN_OF_TIME_LOCALDATETIME, false, 10000);
    public static final AmediaRequestParametere PING = new AmediaRequestParametere(LocalDateTime.now(), false, 1);

    AmediaRequestParametere(LocalDateTime sisteModifiedDate, boolean medInnhold, int resultSize) {
        this.sisteModifiedDate = modifisertDatoMedBuffertid(sisteModifiedDate);
        this.transactionType = DEFAULT_TRANSACTION_TYPE;
        this.sortering = DEFAULT_SORTERING;
        this.medInnhold = Boolean.valueOf(medInnhold).toString();
        this.maxAntallTreff = Integer.toString(resultSize);
    }


    String asString() {
        String utenEncoding = StringSubstitutor
            .replace(AMEDIA_REQUEST_PARAMETERS, lagStringMapAvKlasseparameterene());
        String medCustomEncoding =
            utenEncoding
                .replace(" ", "%20")
                .replace("+", "%2B");

        return "?q=" + medCustomEncoding;
    }

    /**
     * Siden java ikke har en god templating innebygget, der vi kan ha forklarende navn i templatet
     * med en klasse som input, lager vi et hashmap med feltverdiene i klassen, til bruk av
     * StrSubstritutor.
     */
    private Map<String, String> lagStringMapAvKlasseparameterene() {
        return Lists.newArrayList(this.getClass().getDeclaredFields())
            .stream()
            .filter(field ->
                String.class.isAssignableFrom(field.getType()) &&
                    !Modifier.isStatic(field.getModifiers()))
            .collect(Collectors.toMap(Field::getName, field -> {
                try {
                    Object object = field.get(this);
                    return object != null ? (String) object : null;
                } catch (IllegalAccessException e) {
                    LOG.error(
                        "Kunne ikke lage requestParameter, feilet for felt {}, implementeringsfeil?",
                        field.getName());
                    return "";
                }
            }));
    }

    /*
        Legger inn en buffer
     */
    private String modifisertDatoMedBuffertid(LocalDateTime sisteModifiserteDato) {
        if (sisteModifiserteDato == null) {
            return AmediaDateConverter.toStringUrlEncoded(DAWN_OF_TIME_LOCALDATETIME);
        }

        return AmediaDateConverter.toStringUrlEncoded(
            sisteModifiserteDato.minusMinutes(MODIFISERT_DATO_BUFFER_MINUTT));
    }

}
