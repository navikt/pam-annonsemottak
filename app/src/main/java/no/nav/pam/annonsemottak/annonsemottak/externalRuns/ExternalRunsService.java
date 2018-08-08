package no.nav.pam.annonsemottak.annonsemottak.externalRuns;


import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ExternalRunsService {

    private final static Logger LOG = LoggerFactory.getLogger(ExternalRunsService.class);

    private final ExternalRunRepository externalRunRepository;

    @Inject
    public ExternalRunsService(ExternalRunRepository externalRunRepository){
        this.externalRunRepository = externalRunRepository;
    }

    public ExternalRun retrieveExternalRun(String externalRunName){
        LOG.info("Running retrieveExternalRun for {} {} with received list {}", externalRunName);
        ExternalRun externalRun = externalRunRepository.findByName(externalRunName);

        return externalRun;
    }

    public DateTime findLastRunForRunName(String externalRunName){
        LOG.info("Running findLastRunForRunName for {}", externalRunName);
        DateTime lastRun = externalRunRepository.findLastRunForRunName(externalRunName);

        return lastRun;
    }

    public void save(ExternalRun externalRun){
        externalRunRepository.save(externalRun);
    }


    public ExternalRun findByNameAndMedium(String externalRunName, String externalRunMedium) {
        ExternalRun externalRun = externalRunRepository.findByNameAndMedium(externalRunName, externalRunMedium);

        return externalRun;
    }

}
