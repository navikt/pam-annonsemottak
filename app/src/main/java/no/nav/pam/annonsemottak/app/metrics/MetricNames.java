package no.nav.pam.annonsemottak.app.metrics;

public class MetricNames {
    public static final String ADS_COLLECTED = "annonsemottak.ads.collected.v2";

    public static final String ADS_COLLECTED_AMEDIA = ADS_COLLECTED + ".amedia";
    public static final String ADS_COLLECTED_AMEDIA_TOTAL = ADS_COLLECTED_AMEDIA + ".total";
    public static final String ADS_COLLECTED_AMEDIA_NEW = ADS_COLLECTED_AMEDIA + ".new";
    public static final String ADS_COLLECTED_AMEDIA_STOPPED = ADS_COLLECTED_AMEDIA + ".stopped";
    public static final String ADS_COLLECTED_AMEDIA_REJECTED = ADS_COLLECTED_AMEDIA + ".rejected";
    public static final String ADS_COLLECTED_AMEDIA_CHANGED = ADS_COLLECTED_AMEDIA + ".changed";

    public static final String ADS_COLLECTED_DEXI = ADS_COLLECTED + ".dexi";
    public static final String ADS_COLLECTED_DEXI_TOTAL = ADS_COLLECTED_DEXI + ".total";
    public static final String ADS_COLLECTED_DEXI_NEW = ADS_COLLECTED_DEXI + ".new";
    public static final String ADS_COLLECTED_DEXI_STOPPED = ADS_COLLECTED_DEXI + ".stopped";

    public static final String ADS_COLLECTED_FINN = ADS_COLLECTED + ".finn";
    public static final String ADS_COLLECTED_FINN_TOTAL = ADS_COLLECTED_FINN + ".total";
    public static final String ADS_COLLECTED_FINN_NEW = ADS_COLLECTED_FINN + ".new";
    public static final String ADS_COLLECTED_FINN_STOPPED = ADS_COLLECTED_FINN + ".stopped";
    public static final String ADS_COLLECTED_FINN_REJECTED = ADS_COLLECTED_FINN + ".rejected";
    public static final String ADS_COLLECTED_FINN_CHANGED = ADS_COLLECTED_FINN + ".changed";

    public static final String ADS_COLLECTED_SOLR = ADS_COLLECTED + ".solr";
    public static final String ADS_COLLECTED_SOLR_NEW = ADS_COLLECTED_SOLR + ".new";
    public static final String ADS_COLLECTED_SOLR_CHANGED = ADS_COLLECTED_SOLR + ".changed";
    public static final String ADS_COLLECTED_SOLR_TOTAL = ADS_COLLECTED_SOLR + ".total";
    public static final String ADS_DEACTIVATED_SOLR = "annonsemottak.ads.deactivated.solr";


    public static final String ROBOTS_FAILED_METRIC = "annonsemottak.dexi.robots.failed";

    public static final String STATUS_CHANGED_METRIC = "annonsemottak.ad.status.changed";

    public static final String AD_DUPLICATE_METRIC = "annonsemottak.ad.duplicate";

}
