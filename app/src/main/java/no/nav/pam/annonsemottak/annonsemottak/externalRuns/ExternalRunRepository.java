package no.nav.pam.annonsemottak.annonsemottak.externalRuns;


import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;


public interface ExternalRunRepository extends PagingAndSortingRepository<ExternalRun, Long>, JpaSpecificationExecutor<ExternalRun> {

    ExternalRun findByName(String name);

    ExternalRun findByNameAndMedium(String name, String medium);

    @Query("select ex.lastRun from ExternalRun ex where ex.name = ?1")
    LocalDateTime findLastRunForRunName(String name);

}
