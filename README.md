## Beskrivelse

App som henter annonser fra andre kilder som Finn, Amedia

## Kjøre lokalt

`local` er default Spring-profil, så appen kjører lokalt uten ekstra flagg. Den bruker
[Spring Boot sin Docker Compose-støtte](https://docs.spring.io/spring-boot/reference/features/dev-services.html#features.dev-services.docker-compose)
til å automatisk starte Postgres og Kafka (se `compose.yml`) når appen starter, og stopper
dem igjen ved avslutning. Du trenger bare Docker med Compose-pluginen.

```
mvn spring-boot:run
```

Appen starter på http://localhost:9016. Flyway migrerer databasen, og outbox-relayet
publiserer til den lokale Kafka-brokeren (`localhost:9092`).

### Kall mot 3.-partstjenester (Finn / Amedia)

Appen starter uten hemmeligheter. For å kalle de ekte tjenestene, sett de samme
miljøvariablene som nais injiserer fra `pam-annonsemottak-secret`:

```
AMEDIA_API_KEY=... FINN_API_PASSWORD=... mvn spring-boot:run
```
