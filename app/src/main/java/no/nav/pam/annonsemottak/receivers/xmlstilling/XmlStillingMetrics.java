package no.nav.pam.annonsemottak.receivers.xmlstilling;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.pam.annonsemottak.receivers.Kilde;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static no.nav.pam.annonsemottak.app.metrics.MetricNames.*;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.CHANGED;
import static no.nav.pam.annonsemottak.receivers.xmlstilling.Stillinger.Gruppe.NEW;

@Component
class XmlStillingMetrics {

    private final MeterRegistry meterRegistry;

    @Inject
    XmlStillingMetrics(MeterRegistry meterRegistry) {

        this.meterRegistry = meterRegistry;
    }

    void registerFor(Stillinger stillinger) {

        String [] tags = {"source", Kilde.XML_STILLING.toString(), "origin", "XML_STILLING"};

        meterRegistry.counter(ADS_COLLECTED_NEW, tags).increment(stillinger.size(NEW));
        meterRegistry.counter(ADS_COLLECTED_STOPPED, tags).increment(0);
        meterRegistry.counter(ADS_COLLECTED_DUPLICATED, tags).increment(0);
        meterRegistry.counter(ADS_COLLECTED_CHANGED, tags).increment(stillinger.size(CHANGED));

    }

}
