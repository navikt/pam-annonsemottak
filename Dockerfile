FROM navikt/java:8
COPY app/target/pam-annonsemottak-app-*.jar /app/app.jar
EXPOSE 9016