package no.nav.pam.annonsemottak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Just a place to put configuration management code, used by {@link StillingJettyRunner}.
 */
public class ConfigurationHelper {

    private static final String[] LOCATIONS = {
            System.getProperty("user.home") + File.separator + "stilling.properties",
            System.getProperty("user.home") + File.separator + "database.properties"
    };
    private static Logger LOG = LoggerFactory.getLogger(ConfigurationHelper.class);

    static void setConfiguration()
            throws IOException {
        boolean found = false;
        for (String location : LOCATIONS) {
            LOG.info("Looking for configuration in {}", location);
            File file = new File(location);
            if (file.exists()) {
                LOG.info("Found!");
                loadConfigurationFrom(file);
                found = true;
                break;
            }
        }
        if (!found) {
            LOG.error("No configuration found!");
        }
    }

    private static Properties loadConfigurationFrom(File file)
            throws IOException {
        Properties configuration = new Properties();
        try (FileReader reader = new FileReader(file)) {
            configuration.load(reader);
        }
        configuration.entrySet().forEach(entry -> {
            LOG.info("Configuring {} = {} (was {})", entry.getKey(), entry.getValue(), System.getProperty((String) entry.getKey()));
            System.setProperty(entry.getKey().toString(), entry.getValue().toString());
        });
        return configuration;
    }

}
