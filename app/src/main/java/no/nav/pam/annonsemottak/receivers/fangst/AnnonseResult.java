package no.nav.pam.annonsemottak.receivers.fangst;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hold all results from a import run.
 */
public class AnnonseResult {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AnnonseResult.class);

    private final List<Stilling> modifyList;
    private final List<Stilling> stopList;
    private final List<Stilling> expiredList;
    private final List<Stilling> newList;
    private final List<Stilling> duplicateList;

    public AnnonseResult() {
        this.newList = new ArrayList<>();
        this.modifyList = new ArrayList<>();
        this.expiredList = new ArrayList<>();
        this.stopList = new ArrayList<>();
        this.duplicateList = new ArrayList<>();
    }

    public AnnonseResult(List<Stilling> modifyList, List<Stilling> stopList,
                         List<Stilling> expiredList, List<Stilling> newList,
                         List<Stilling> duplicateList) {
        this.modifyList = modifyList;
        this.stopList = stopList;
        this.expiredList = expiredList;
        this.newList = newList;
        this.duplicateList = duplicateList;
    }

    void handleIfModifiedAd(Map<String, Stilling> activeMap, Stilling receive) {
        Stilling active = activeMap.get(receive.getExternalId());
        if (!receive.getHash().equals(active.getHash())) {
            receive = receive.merge(active);
            modifyList.add(receive);
        } else {
            LOG.info("Stilling markert for modifiseing med externalid {} har lik hash med activeMap...", receive.getExternalId());
        }

    }


    void handleIfNotActiveAd(Stilling receive, Optional<Stilling> notActive) {
        // if ad is stopped, and reactivated we must handle that.
        if (notActive.isPresent()) {
            LOG.info("Ad {} was stopped/deactivated but is now reactivated", notActive.get().getExternalId());
            receive = receive.merge(notActive.get());
            modifyList.add(receive);
        } else {
            newList.add(receive);
        }
    }

    public List<Stilling> getModifyList() {
        return modifyList;
    }

    public List<Stilling> getStopList() {
        return stopList;
    }

    public List<Stilling> getNewList() {
        return newList;
    }

    public List<Stilling> getDuplicateList() {
        return duplicateList;
    }

    public List<Stilling> getExpiredList() {
        return expiredList;
    }

    public List<Stilling> getAll() {
        return Stream
            .of(modifyList.stream(), newList.stream(), stopList.stream(), expiredList.stream(), duplicateList.stream())
            .reduce(Stream::concat)
            .orElseGet(Stream::empty)
            .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return
            "New: " + newList.size() +
            ", Modified: " + modifyList.size() +
            ", Stopped: " + stopList.size() +
            ", Expired=" + expiredList.size() +
            ", Duplicated=" + duplicateList.size();
    }
}
