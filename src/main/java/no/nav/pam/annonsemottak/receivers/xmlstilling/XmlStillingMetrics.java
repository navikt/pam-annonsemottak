package no.nav.pam.annonsemottak.receivers.xmlstilling;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.app.metrics.AnnonseMottakProbe;
import no.nav.pam.annonsemottak.receivers.Kilde;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.CHANGED;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.NEW;

@Component
class XmlStillingMetrics {

    private final MeterRegistry meterRegistry;
    private final AnnonseMottakProbe probe;

    @Inject
    XmlStillingMetrics(MeterRegistry meterRegistry, AnnonseMottakProbe probe) {

        this.meterRegistry = meterRegistry;
        this.probe = probe;
    }

    void registerFor(Stillinger stillinger) {

        String [] tags = {"source", Kilde.XML_STILLING.toString(), "origin", "XML_STILLING"};

        meterRegistry.counter(ADS_COLLECTED_NEW, tags).increment(stillinger.size(NEW));
        meterRegistry.counter(ADS_COLLECTED_STOPPED, tags).increment(0);
        meterRegistry.counter(ADS_COLLECTED_DUPLICATED, tags).increment(0);
        meterRegistry.counter(ADS_COLLECTED_CHANGED, tags).increment(stillinger.size(CHANGED));

        probe.newAdPoint((long)stillinger.size(NEW), Kilde.XML_STILLING.toString(), "XML_STILLING");
        probe.stoppedAdPoint(0L, Kilde.XML_STILLING.toString(), "XML_STILLING");
        probe.duplicateAdPoint(0L, Kilde.XML_STILLING.toString(), "XML_STILLING");
        probe.changedAdPoint((long)stillinger.size(CHANGED), Kilde.XML_STILLING.toString(), "XML_STILLING");
    }

}
