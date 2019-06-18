package no.nav.pam.annonsemottak.app.rest;


import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.amedia.AmediaConnector;
import no.nav.pam.annonsemottak.receivers.dexi.DexiConnector;
import no.nav.pam.annonsemottak.receivers.finn.FinnConnector;
import no.nav.pam.annonsemottak.receivers.polaris.PolarisConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusController {

    private final FinnConnector finnConnector;

    private final DexiConnector dexiConnector;

    private final AmediaConnector amediaConnector;

    private final PolarisConnector polarisConnector;

    @Autowired
    public StatusController(FinnConnector finnConnector, DexiConnector dexiConnector, AmediaConnector amediaConnector, PolarisConnector polarisConnector) {
        this.finnConnector = finnConnector;
        this.dexiConnector = dexiConnector;
        this.amediaConnector = amediaConnector;
        this.polarisConnector = polarisConnector;
    }


    @GetMapping(path = "/isAlive")
    public String isAlive() {
        return "OK";
    }

    @GetMapping(path = "/isReady")
    public String isReady() {
        return "OK";
    }

    @GetMapping(path = "/amIOK")
    public String amIOk() {

        if (isDexiOK()
                && isAmediaOK()
                && isFinnOK()
                && isPolarisOK()
                ) {
            return "OK";
        }

        return "NOT OK";
    }

    @GetMapping(path = "/isSourcePingOK")
    public ResponseEntity pingSourcesAndGetStatus() {

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(Kilde.DEXI.value(), statusToString(isDexiOK()));
        statusMap.put(Kilde.FINN.value(), statusToString(isFinnOK()));
        statusMap.put(Kilde.AMEDIA.value(), statusToString(isAmediaOK()));
        statusMap.put(Kilde.POLARIS.value(), statusToString(isPolarisOK()));

        return ResponseEntity.ok(statusMap);
    }

    private String statusToString(boolean value) {
        return (value) ? "OK" : "NOT OK";
    }

    private boolean isFinnOK() {
        return finnConnector.isPingSuccessful();
    }

    private boolean isDexiOK() {
        return dexiConnector.isPingSuccessful();
    }

    private boolean isAmediaOK() {
        return amediaConnector.isPingSuccessful();
    }

    private boolean isPolarisOK() { return polarisConnector.isPingSuccessful(); }
}
