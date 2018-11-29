package no.nav.pam.annonsemottak.annonsemottak.polaris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolarisContact {

    public String name;
    public String lastname;
    public String title;
    public String email;
    public String telephone;
    public String mobile;
}
