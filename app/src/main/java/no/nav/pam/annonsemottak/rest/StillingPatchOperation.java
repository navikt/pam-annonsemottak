package no.nav.pam.annonsemottak.rest;

import java.util.Map;

public class StillingPatchOperation {

    private String uuid;
    private Map<String, String> change;

    public StillingPatchOperation() {
    }

    StillingPatchOperation(String uuid, Map<String, String> change) {
        this.uuid = uuid;
        this.change = change;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, String> getChange() {
        return change;
    }

    public void setChange(Map<String, String> change) {
        this.change = change;
    }

}
