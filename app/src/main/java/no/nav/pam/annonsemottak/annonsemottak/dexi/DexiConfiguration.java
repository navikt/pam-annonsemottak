package no.nav.pam.annonsemottak.annonsemottak.dexi;

import com.fasterxml.jackson.annotation.JsonProperty;
import no.nav.pam.annonsemottak.annonsemottak.Kilde;

public class DexiConfiguration {

    public static final String PRODUCTION = "produksjon";
    public static final String KILDE = Kilde.DEXI.value();

    private final String robotId;
    private final String robotName;
    private final String jobId;
    private final String jobName;

    public DexiConfiguration(String robotId, String robotName, String jobId, String jobName) {
        this.robotId = robotId;
        this.robotName = robotName;
        this.jobId = jobId;
        this.jobName = jobName;
    }

    @JsonProperty("jobName")
    public String getJobName() {
        return jobName;
    }

    @JsonProperty("robotId")
    public String getRobotId() {
        return robotId;
    }

    @JsonProperty("robotName")
    public String getRobotName() {
        return robotName;
    }

    @JsonProperty("jobId")
    public String getJobId() {
        return jobId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", robotId='" + robotId + '\'' +
                ", robotName='" + robotName + '\'' +
                '}';
    }
}
