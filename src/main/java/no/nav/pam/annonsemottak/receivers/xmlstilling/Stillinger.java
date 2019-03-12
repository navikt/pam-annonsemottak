package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.Stilling;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.CHANGED;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.NEW;

class Stillinger {

    enum Gruppe {
        NEW,
        CHANGED,
        @Deprecated
        CHANGED_ARENA,
        UNCHANGED
    }

    private static class StillingKey {

        private final String kilde, medium, externalId;

        StillingKey(Stilling stilling) {
            this.kilde = stilling.getKilde();
            this.medium = stilling.getMedium();
            this.externalId = stilling.getExternalId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StillingKey key = (StillingKey) o;
            return Objects.equals(kilde, key.kilde) &&
                    Objects.equals(medium, key.medium) &&
                    Objects.equals(externalId, key.externalId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kilde, medium, externalId);
        }
    }

    private Map<Gruppe, List<Stilling>> stillinger;

    Stillinger(
            final List<Stilling> stillinger,
            final Function<Stilling, Gruppe> grupperingStrategi) {

        this.stillinger = stillinger == null ?
                new HashMap<>() :
                stillinger.stream().collect(groupingBy(grupperingStrategi));

        moveDuplicateNewsToChanged();

    }

    private void moveDuplicateNewsToChanged() {

        get(CHANGED).addAll(
                get(NEW).stream()
                        .filter(distinctStilling().negate())
                        .collect(Collectors.toList())
        );

        get(NEW).removeIf(distinctStilling().negate());
    }

    private static Predicate<Stilling> distinctStilling() {
        Set<StillingKey> seen = ConcurrentHashMap.newKeySet();
        return stilling -> seen.add(new StillingKey(stilling));
    }

    List<Stilling> get(Gruppe gruppe) {
        return stillinger.computeIfAbsent(gruppe, any -> new ArrayList<>());
    }

    int size(Gruppe gruppe) {
        return get(gruppe).size();
    }

    Optional<LocalDateTime> latestDate() {
        return merge(Gruppe.values()).stream()
                .map(Stilling::getSystemModifiedDate)
                .max(LocalDateTime::compareTo);
    }

    List<Stilling> asList() {
        return merge(Gruppe.values());
    }


    List<Stilling> merge(Gruppe... grupper) {
        return Arrays.stream(grupper)
                .map(stillinger::get)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
