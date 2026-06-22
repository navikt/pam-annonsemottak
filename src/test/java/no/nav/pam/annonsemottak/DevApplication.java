package no.nav.pam.annonsemottak;

import org.springframework.boot.SpringApplication;

public class DevApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
