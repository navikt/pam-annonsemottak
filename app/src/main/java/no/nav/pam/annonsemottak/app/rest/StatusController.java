package no.nav.pam.annonsemottak.app.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private static final Logger LOG = LoggerFactory.getLogger(StatusController.class);

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
        return "OK";
    }

}
