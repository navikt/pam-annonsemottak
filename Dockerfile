FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21

COPY target/pam-annonsemottak-*.jar /app/app.jar
EXPOSE 9016
COPY scripts/init-env.sh /init-scripts/init-env.sh

ENV JAVA_OPTS="-Xms768m -Xmx1024m"
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

CMD ["-jar","/app/app.jar", "-Dspring.profiles.active=prod"]
