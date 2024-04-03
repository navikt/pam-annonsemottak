## Beskrivelse

App som henter annonser fra andre kilder som Finn, Amedia

### Running in localhost

Copy `pam-annonsemottak\src\test\resources\application-dev.yml` to your home folder (rename to pamannonsemottak-dev), and add your developer keys etc. 

#### Backend
```
mvn clean install
mvn spring-boot:run -Dspring.profiles.active=dev -Dspring.config.location=${user.home}/pamannonsemottak-dev.yml
```

#### Intellij
Run>Edit configurations
Add new configurations, choose maven
Then enter:
```
spring-boot:run -Dspring.profiles.active=dev -Dspring.config.location=${user.home}/pamannonsemottak-dev.yml
```
on the command line field. 

