FROM navikt/java:8
COPY app/target/pam-annonsemottak-app-*.jar /app/app.jar
EXPOSE 9016
ENV JAVA_OPTS="-Xms768m -Xmx1024m"