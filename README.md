# PAM-annonsemottak

App som henter annonser fra andre kilder som Finn, Amedia, Dexi (web-crawl)


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

# Developer documentation

### Teste og prodsette nye Dexi crawls

Teste opprettet dexi jobb i StillingsAdmin
1.    Åpne f.eks. Postman og send følgende endepunkt som POST request (bytt ut www.vinje.kommune.no i endepunktet med aktuelt robotnavn): 
https://pam-annonsemottak.nais.oera-q.local/internal/annonsemottak/dexi/robots/www.vinje.kommune.no/configurations/test/results
2.    Responsen forteller om det gikk bra. F.eks.:
   ```
   {
     "stillingerHentet": 11,
     "stillingerLagret": 11,
     "millisekunderBrukt": 2711
   }
   ```
3.    Sjekk at stillingene har kommet inn i StillingsAdmin: https://pam-nss-admin.nais.preprod.local
4.    Sjekke om de nye dexi stillingene har kommet inn i StillingsAdmin. Si ifra til produkteier at det er mulig å verifisere om det er ønskelig
5.    Produksjonsetting skjer ved å flytte roboten til under mappen PRODUKSJON i dexi og endre navnet på konfigurasjonen av roboten til "produksjon".
