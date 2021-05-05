package no.nav.pam.annonsemottak.stilling;

import org.springframework.data.domain.Sort;

/**
 * Gir funksjonalitet for å kunne sortere en stilling etter 'orderBy' og direction.
 */
public class Sortering {

    private final OrderBy orderBy;
    private final OrderDirection orderDirection;

    /**
     * Oppretter en Sortering. Default sortering vil være etter MOTTATTDATO, den eldste først.
     */
    public static Sortering valueOf(OrderBy orderBy, OrderDirection orderDirection) {
        return new Sortering(
                orderBy != null ? orderBy : OrderBy.MOTTATTDATO,
                orderDirection != null ? orderDirection : OrderDirection.ASC);
    }

    private Sortering(OrderBy orderBy, OrderDirection orderDirection) {
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    public Sort asSort() {
        return Sort.by(direction(), orderBy.propertyName());
    }

    private Sort.Direction direction() {
        if (orderDirection.equals(OrderDirection.ASC)) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }

    /**
     * propertyName henviser til feltnavnet i Stilling. Noen felter finner også i Stilling -> Saksbehandling.
     */
    public enum OrderBy {
        DATO("dueDate"),
        MOTTATTDATO("created"),
        ARBEIDSGIVER("employer"),
        STED("place"),
        TITTEL("title"),
        STATUS("saksbehandling.status"),
        ANNONSESTATUS("annonsestatus"),
        SOKNADSFRIST("dueDate"),
        MODIFISERTDATO("updated"),
        KOMMENTARER("saksbehandling.kommentarer"),
        SAKSBEHANDLER("saksbehandling.saksbehandler");

        private final String propertyName;

        OrderBy(String propertyName) {
            this.propertyName = propertyName;
        }

        public static OrderBy nullSafeValueOf(String value) {
            if (value == null) {
                return MOTTATTDATO;
            }
            return valueOf(value.toUpperCase());
        }

        private String propertyName() {
            return propertyName;
        }
    }

    public enum OrderDirection {
        ASC,
        DESC;

        public static OrderDirection nullSafeValueOf(String value) {
            if (value == null) {
                return ASC;
            }
            return valueOf(value.toUpperCase());
        }
    }
}
