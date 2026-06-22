FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

COPY target/pam-annonsemottak-*.jar /app/app.jar
EXPOSE 9016
COPY scripts/init-env.sh /init-scripts/init-env.sh

ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

CMD ["-jar","/app/app.jar", "-Dspring.profiles.active=prod"]
