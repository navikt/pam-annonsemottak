package no.nav.pam.annonsemottak.stilling.rest.payloads;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorPayload extends BasicPayload<String> {

    private final ErrorMeta meta;

    public ErrorPayload(DefinedErrors cause) {
        super(null);
        meta = new ErrorMeta(cause);
    }

    @Override
    public Meta getMeta() {
        return meta;
    }

    public enum DefinedErrors {

        // Splitting defined codes into CLIENT_* and SERVER_*. Possibly OTHER_* if we want to shift blame to another system...
        CLIENT_ILLEGAL_UPDATE("KLIENT_UGYLDIG_OPPDATERING"),
        SERVER_INTERNAL("TJENESTE_INTERN_FEIL");

        private final String code;

        DefinedErrors(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

    }

    public class ErrorMeta extends Meta {

        private final ErrorMetaContents error;

        private ErrorMeta(DefinedErrors cause) {
            this.error = new ErrorMetaContents(cause);
        }

        @JsonProperty("error")
        public ErrorMetaContents getError() {
            return error;
        }

        public class ErrorMetaContents {
            private final String code;

            private ErrorMetaContents(DefinedErrors error) {
                this.code = error.getCode();
            }

            @JsonProperty("code")
            public String getCode() {
                return code;
            }
        }

    }

}
