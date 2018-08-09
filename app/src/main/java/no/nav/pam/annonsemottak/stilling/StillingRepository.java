package no.nav.pam.annonsemottak.stilling;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StillingRepository extends PagingAndSortingRepository<Stilling, Long>, JpaSpecificationExecutor<Stilling> {

    @Query("select s.uuid from Stilling s")
    List<String> findUuids();

    @Query("select case when count(s) > 0 then true else false end from Stilling s where s.hash = ?1")
    Boolean hashExists(String hash);

    Stilling findByUuid(String uuid);

    List<Stilling> findByAnnonseStatus(AnnonseStatus annonseStatus);

    List<Stilling> findBySaksbehandlingStatusAndAnnonseStatus(Status status, AnnonseStatus annonseStatus);

    List<Stilling> findByKildeAndMediumAndAnnonseStatus(String kilde, String medium, AnnonseStatus status);

    Stilling findByKildeAndMediumAndExternalId(String kilde, String medium, String externalId);

    @Query("select count(s) from  Stilling s where s.saksbehandling.status = ?2 and s.saksbehandling.saksbehandler = ?1")
    Long numberOfActiv(String saksbehandler, Status status);

    @Query("select s from Stilling s where s.updated >= ?1")
    Page<Stilling> findUpdatedAfter(LocalDateTime updateDate, Pageable pageable);

    List<Stilling> findByKildeAndAnnonseStatus(String kilde, AnnonseStatus status);
}
