FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25

RUN useradd -r -u 1001 appuser

WORKDIR /app

COPY target/pam-annonsemottak-*.jar /app/app.jar

RUN chown -R appuser:appuser /app

USER appuser

ENV JAVA_TOOL_OPTIONS="-XX:InitialRAMPercentage=25 -XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 9016

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
