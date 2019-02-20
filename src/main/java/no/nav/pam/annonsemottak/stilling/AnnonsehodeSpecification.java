package no.nav.pam.annonsemottak.stilling;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AnnonsehodeSpecification implements Specification<Stilling> {

    private final String arbeidsgiver;
    private final String arbeidssted;
    private final String stillingstittel;
    private final String saksbehandler;
    private final String kilde;
    private final Status status;
    private final AnnonseStatus annonseStatus;

    public static Specification<Stilling> withSpecification(
            String arbeidsgiver,
            String arbeidssted,
            String stillingstittel,
            String saksbehandler,
            String kilde,
            String status,
            String annonsestatus) {

        return Specification.where(new AnnonsehodeSpecification(
                arbeidsgiver,
                arbeidssted,
                stillingstittel,
                saksbehandler,
                kilde,
                status,
                annonsestatus));
    }

    private AnnonsehodeSpecification(
            String arbeidsgiver,
            String arbeidssted,
            String stillingstittel,
            String saksbehandler,
            String kilde,
            String status,
            String annonsestatus
    ) {
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidssted = arbeidssted;
        this.stillingstittel = stillingstittel;
        this.saksbehandler = saksbehandler;
        this.kilde = kilde;
        this.status = (status != null)? Status.valueOfStatuskode(status) : null;
        this.annonseStatus = (annonsestatus != null)? AnnonseStatus.valueOfAnnonseStatusCode(annonsestatus) : null;
    }

    @Override
    public Predicate toPredicate(Root<Stilling> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        if (arbeidsgiver != null) {
            predicates.add(builder.like(builder.lower(root.get("employer")), "%" + arbeidsgiver.toLowerCase() + "%"));
        }
        if (arbeidssted != null) {
            predicates.add(builder.like(builder.lower(root.get("place")), "%" + arbeidssted.toLowerCase() + "%"));
        }
        if (stillingstittel != null) {
            predicates.add(builder.like(builder.lower(root.get("title")), "%" + stillingstittel.toLowerCase() + "%"));
        }
        if (saksbehandler != null) {
            predicates.add(builder.like(builder.lower(root.get("saksbehandling").get("saksbehandler")), "%" + saksbehandler.toLowerCase() + "%"));
        }
        if (kilde != null) {
            // TODO: kan fjernes når stillingsolr fjernes
            if (kilde.startsWith("!")) {
                predicates.add(builder.notLike(builder.lower(root.get("kilde")), "%" + kilde.substring(1).toLowerCase() + "%"));
            } else {
                predicates.add(builder.like(builder.lower(root.get("kilde")), "%" + kilde.toLowerCase() + "%"));
            }
        }
        if (status != null) {
            predicates.add(builder.equal(root.get("saksbehandling").get("status"), status));
        }
        if (annonseStatus != null) {
            predicates.add(builder.equal(root.get("annonseStatus"), annonseStatus));
        }

        return builder.and(predicates.toArray(new Predicate[]{}));
    }

}
