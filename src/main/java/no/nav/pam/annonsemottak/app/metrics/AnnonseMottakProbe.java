package no.nav.pam.annonsemottak.app.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;

@Service
public class AnnonseMottakProbe {

    private final MeterRegistry meterRegistry;

    @Autowired
    AnnonseMottakProbe(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void addMetricsCounters(String kilde, String medium, int newSize, int stopSize, int dupSize, int modifySize) {

        newAdPoint((long)newSize, kilde, medium);
        stoppedAdPoint((long)stopSize, kilde, medium);
        duplicateAdPoint((long)dupSize, kilde, medium);
        changedAdPoint((long)modifySize, kilde, medium);
    }

    public void newFailedPoint(String kilde, String medium) {
        meterRegistry.counter(ADS_COLLECTED_FAILED, "source", kilde, "origin", medium).increment();
    }

    void duplicateAdPoint(Long count, String kilde, String medium) {
        meterRegistry.counter(ADS_COLLECTED_DUPLICATED, "source", kilde, "origin", medium).increment(count);
    }

    void newAdPoint(Long count, String kilde, String medium) {
        meterRegistry.counter(ADS_COLLECTED_NEW, "source", kilde, "origin", medium).increment(count);
    }

    void stoppedAdPoint(Long count, String kilde, String medium) {
        meterRegistry.counter(ADS_COLLECTED_STOPPED, "source", kilde, "origin", medium).increment(count);
    }

    void changedAdPoint(Long count, String kilde, String medium) {
        meterRegistry.counter(ADS_COLLECTED_CHANGED, "source", kilde, "origin", medium).increment(count);
    }
}


