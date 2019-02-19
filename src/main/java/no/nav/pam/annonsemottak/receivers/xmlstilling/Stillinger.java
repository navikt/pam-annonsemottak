package no.nav.pam.annonsemottak.receivers.xmlstilling;

import no.nav.pam.annonsemottak.stilling.Stilling;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

class Stillinger {

    enum Gruppe {
        NEW, CHANGED, UNCHANGED
    }

    private final Map<Gruppe, List<Stilling>> stillinger;

    Stillinger(
            final List<Stilling> stillinger,
            final Function<Stilling, Gruppe> grupperingStrategi) {

        this.stillinger = stillinger == null ?
                Collections.emptyMap() :
                stillinger.stream().collect(groupingBy(grupperingStrategi));

    }

    Optional<List<Stilling>> get(Gruppe gruppe) {
        return Optional.ofNullable(stillinger.get(gruppe));
    }

    int size(Gruppe gruppe) {
        return get(gruppe).map(List::size).orElse(0);
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
