package no.nav.pam.annonsemottak.stilling;

import org.springframework.data.domain.PageRequest;

public class AnnonsehodePageRequest {

    private static final int DEFAULT_PAGE_SIZE = 200;

    public static PageRequest withPageRequest(String page, String size, String property, String direction) {
        int numericPage = parseNumericValue(page, 0, 0, Integer.MAX_VALUE);
        int numericSize = parseNumericValue(size, DEFAULT_PAGE_SIZE, 1, Integer.MAX_VALUE);
        return PageRequest.of(
                numericPage,
                numericSize < 1 ? DEFAULT_PAGE_SIZE : numericSize,
                Sortering.valueOf(
                        Sortering.OrderBy.nullSafeValueOf(property),
                        Sortering.OrderDirection.nullSafeValueOf(direction)
                ).asSort());
    }

    private static int parseNumericValue(String textValue, int defaultValue, int minimumValue, int maximumValue) {
        if (textValue == null) {
            return defaultValue;
        }
        try {
            int numbericValue = Integer.valueOf(textValue);
            if (numbericValue < minimumValue) {
                numbericValue = minimumValue;
            } else if (numbericValue > maximumValue) {
                numbericValue = maximumValue;
            }
            return numbericValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
