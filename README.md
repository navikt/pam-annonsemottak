# PAM-annonsemottak

App som henter annonser fra andre kilder som Finn, Amedia, Dexi (web-crawl)


### Running in localhost

#### Backend
```
mvn clean install
cd app
mvn spring-boot:run -Dspring.profiles.active=dev -Dspring.config.location=${user.home}/pamannonsemmotak-dev.yml
```

#### Intellij
Run>Edit configurations
Add new configurations, choose maven
Then enter:
```
spring-boot:run -Dspring.profiles.active=dev -Dspring.config.location=${user.home}/pamannonsemmotak-dev.yml
```
on the command line field. 

