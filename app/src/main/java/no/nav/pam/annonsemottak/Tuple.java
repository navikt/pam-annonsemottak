package no.nav.pam.annonsemottak;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Tuple<X, Y> implements Serializable {

    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public static <X, Y> Tuple<X, Y> of(X x, Y y) {
        return new Tuple<X, Y>(x, y);
    }


    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj, false);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public String toString() {
        return "Tuple(" + x + ", " + y + ")";
    }

}
