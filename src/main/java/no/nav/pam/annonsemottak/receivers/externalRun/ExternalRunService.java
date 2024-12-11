package no.nav.pam.annonsemottak.receivers.externalRun;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExternalRunService {

    private final static Logger LOG = LoggerFactory.getLogger(ExternalRunService.class);

    private final ExternalRunRepository externalRunRepository;

    @Autowired
    public ExternalRunService(ExternalRunRepository externalRunRepository){
        this.externalRunRepository = externalRunRepository;
    }

    public Iterable<ExternalRun> retrieveAll(){
        return externalRunRepository.findAll();
    }

    public ExternalRun retrieveExternalRun(String externalRunName){
        LOG.debug("Running retrieveExternalRun for {}", externalRunName);
        ExternalRun externalRun = externalRunRepository.findByName(externalRunName);

        return externalRun;
    }

    public LocalDateTime findLastRunForRunName(String externalRunName){
        LOG.debug("Running findLastRunForRunName for {}", externalRunName);
        LocalDateTime lastRun = externalRunRepository.findLastRunForRunName(externalRunName);

        return lastRun;
    }

    public void save(ExternalRun externalRun){
        LOG.info("Saving external run for source {} with time {}", externalRun.getName(), externalRun.getLastRun());
        externalRunRepository.save(externalRun);
    }


    public ExternalRun findByNameAndMedium(String externalRunName, String externalRunMedium) {
        ExternalRun externalRun = externalRunRepository.findByNameAndMedium(externalRunName, externalRunMedium);

        return externalRun;
    }

}
