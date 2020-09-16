FROM navikt/java:12
COPY target/pam-annonsemottak-*.jar /app/app.jar
COPY pam-annonsemottak-migration/target/pam-annonsemottak-migration-*.jar /app/pam-annonsemottak-migration.jar
EXPOSE 9016
COPY scripts/init-env.sh /init-scripts/init-env.sh
ENV JAVA_OPTS="-Xms768m -Xmx1024m"
