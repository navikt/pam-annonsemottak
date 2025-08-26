FROM eclipse-temurin:21-jre-alpine

COPY target/pam-annonsemottak-*.jar /app/app.jar
COPY scripts/init-env.sh /init-scripts/init-env.sh
EXPOSE 9016

ENV JAVA_OPTS="-Xms768m -Xmx1024m"
ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"

ENTRYPOINT ["java","-jar","/app/app.jar"]
