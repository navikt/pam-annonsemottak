package no.nav.pam.annonsemottak.receivers.polaris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolarisLocation {

    public String street;
    public String postal;
    public String city;
    public String municipality;
    public String latitude;
    public String longitude;
}

