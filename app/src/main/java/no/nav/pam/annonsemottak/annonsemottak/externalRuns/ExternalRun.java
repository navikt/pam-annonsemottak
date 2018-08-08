package no.nav.pam.annonsemottak.annonsemottak.externalRuns;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "EXTERNALRUN")
@SequenceGenerator(name = "externalrun_sequence", sequenceName = "externalrun_sequence", allocationSize = 1)
public class ExternalRun {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "externalrun_sequence")
    private Long id;

    @NotNull
    private String name;

    private String medium;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime lastRun;

    /**
     Default constructor for Hibernate
     */
    protected ExternalRun() {
    }

    public ExternalRun(String name, String medium, DateTime lastRun){
        this.name = name;
        this.medium = medium;
        this.lastRun = lastRun;
    }

    public ExternalRun(Long id, String name, String medium, DateTime lastRun){
        this.id = id;
        this.name = name;
        this.medium = medium;
        this.lastRun = lastRun;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public DateTime getLastRun() {
        return lastRun;
    }

    public void setLastRun(DateTime lastRun) {
        this.lastRun = lastRun;
    }

    public String getName() {

        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
