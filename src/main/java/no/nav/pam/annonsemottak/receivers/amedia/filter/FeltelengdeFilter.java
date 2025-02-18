package no.nav.pam.annonsemottak.receivers.amedia.filter;

import no.nav.pam.annonsemottak.stilling.Stilling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FeltelengdeFilter extends StillingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(FeltelengdeFilter.class);

    private static final Predicate<Stilling> HAR_TILLATTE_FELTLENGDER =
            stilling -> stilling.felterSomOverstigerGrensenPaa255Tegn().isEmpty();

    @Override
    protected List<Stilling> doFilter(List<Stilling> stillinger) {
        List<Stilling> stillingerOk = stillinger.stream()
                .filter(HAR_TILLATTE_FELTLENGDER)
                .collect(Collectors.toList());
        List<Stilling> stillingerAvvist = stillinger.stream()
                .filter(HAR_TILLATTE_FELTLENGDER.negate())
                .collect(Collectors.toList());

        logFilter(
                stillinger.size(),
                stillingerOk.size(),
                stillingerAvvist,
                FilterAarsak.FELTLENGDE_OVERSKREDET);
        logFelterSomBleAvvist(stillingerAvvist);
        return stillingerOk;
    }

    private void logFelterSomBleAvvist(List<Stilling> avvisteStillinger) {
        if (!avvisteStillinger.isEmpty()) {
            for (Stilling stilling : avvisteStillinger) {
                List<Map.Entry<String, String>> felterSomErForlange = stilling.felterSomOverstigerGrensenPaa255Tegn();
                for (Map.Entry<String, String> felt : felterSomErForlange) {
                    LOG.warn("Stilling med externalid '{}' ble filtrert bort, fordi feltet '{}' har lengde {} mens kun 255 er tillatt. Feltverdi: '{}'",
                            stilling.getExternalId(), felt.getKey(), felt.getValue().length(), felt.getValue());
                }
            }
        }
    }

}
