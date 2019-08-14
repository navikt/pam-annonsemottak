FROM navikt/java:12
COPY target/pam-annonsemottak-*.jar /app/app.jar
EXPOSE 9016
ENV JAVA_OPTS="-Xms768m -Xmx1024m"