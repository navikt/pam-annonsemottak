package no.nav.pam.annonsemottak.receivers.polaris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolarisCategory {

    @JsonProperty("Name")
    public String name;

    @JsonProperty("SubCategories")
    public List<PolarisCategory> subCategories;

}