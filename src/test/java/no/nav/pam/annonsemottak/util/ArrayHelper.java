package no.nav.pam.annonsemottak.util;

public abstract class ArrayHelper {

    private ArrayHelper() {
    }

    @SafeVarargs
    public static <T> T[] of(T... ts) {
        return ts;
    }

}
