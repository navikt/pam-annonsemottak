package no.nav.pam.annonsemottak.app.rest;


import no.nav.pam.annonsemottak.kafka.HealthService;
import no.nav.pam.annonsemottak.receivers.Kilde;
import no.nav.pam.annonsemottak.receivers.amedia.AmediaConnector;
import no.nav.pam.annonsemottak.receivers.finn.FinnConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StatusController {

    private final FinnConnector finnConnector;

    private final AmediaConnector amediaConnector;


    private final HealthService healthService;

    @Autowired
    public StatusController(FinnConnector finnConnector, AmediaConnector amediaConnector, HealthService healthService) {
        this.finnConnector = finnConnector;
        this.amediaConnector = amediaConnector;
        this.healthService = healthService;
    }


    @GetMapping(path = "/isAlive")
    public String isAlive() throws InternalException {
        if (isKafkaOk()) return "OK";
        else throw new InternalException("NOT OK!");
    }

    @GetMapping(path = "/isReady")
    public String isReady() {
        return "OK";
    }

    @GetMapping(path = "/amIOK")
    public String amIOk() {

        if (isAmediaOK()
                && isFinnOK()
                && isKafkaOk()
        ) {
            return "OK";
        }

        return "NOT OK";
    }

    @GetMapping(path = "/isSourcePingOK")
    public ResponseEntity pingSourcesAndGetStatus() {

        Map<String, String> statusMap = new HashMap<>();
        statusMap.put(Kilde.FINN.value(), statusToString(isFinnOK()));
        statusMap.put(Kilde.AMEDIA.value(), statusToString(isAmediaOK()));

        return ResponseEntity.ok(statusMap);
    }

    private String statusToString(boolean value) {
        return (value) ? "OK" : "NOT OK";
    }

    private boolean isFinnOK() {
        return finnConnector.isPingSuccessful();
    }

    private boolean isAmediaOK() {
        return amediaConnector.isPingSuccessful();
    }

    private boolean isKafkaOk() {
        return healthService.isHealthy();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public static class InternalException extends RuntimeException {
        public InternalException(String message) {
            super(message);
        }
    }
}
