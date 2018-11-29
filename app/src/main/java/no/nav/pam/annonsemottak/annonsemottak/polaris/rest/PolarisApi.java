package no.nav.pam.annonsemottak.annonsemottak.polaris.rest;


import no.nav.pam.annonsemottak.annonsemottak.polaris.PolarisConnector;
import no.nav.pam.annonsemottak.api.PathDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(PathDefinition.POLARIS)
public class PolarisApi {

    private static final Logger LOG = LoggerFactory.getLogger(PolarisApi.class);

    private final PolarisConnector polarisConnector;

    @Autowired
    public PolarisApi(PolarisConnector polarisConnector) {
        this.polarisConnector = polarisConnector;
    }

    @GetMapping
    public ResponseEntity ping(){

        return ResponseEntity.ok("");
    }
}
