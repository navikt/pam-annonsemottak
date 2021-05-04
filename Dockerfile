FROM navikt/java:14
COPY target/pam-annonsemottak-*.jar /app/app.jar
EXPOSE 9016
COPY scripts/init-env.sh /init-scripts/init-env.sh
ENV JAVA_OPTS="-Xms768m -Xmx1024m"
