package no.nav.pam.annonsemottak.stilling;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.enkelStilling;
import static no.nav.pam.annonsemottak.stilling.StillingTestdataBuilder.stilling;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Rollback
@Transactional
public class DatabaseScriptsTest {

    @Inject
    private StillingRepository stillingRepository;

    @Test
    public void sanityTest() {
        Map<String, String> keyValueSet = new HashMap<>();
        keyValueSet.put("aKey", "aValue");
        Stilling stilling = enkelStilling()
                .properties(keyValueSet)
                .build();
        stilling = stillingRepository.save(stilling);
        stillingRepository.save(stilling);
        stilling.getProperties().put("hello", "world");
        stillingRepository.save(stilling);
        assertThat(Sets.newHashSet(stillingRepository.findAll()).size(), equalTo(1));
        assertThat(stilling.getProperties().get("hello"), is(equalTo("world")));
        assertThat(stilling.getProperties().get("aKey"), is(equalTo("aValue")));
    }
}
