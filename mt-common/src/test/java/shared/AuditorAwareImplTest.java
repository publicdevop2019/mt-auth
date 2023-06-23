package shared;

import com.mt.common.infrastructure.audit.SpringDataJpaConfig;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuditorAwareImplTest {

    SpringDataJpaConfig.AuditorAwareImpl auditorAware = new SpringDataJpaConfig.AuditorAwareImpl();

    @Test
    public void getCurrentAuditor_noAuth() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        Assertions.assertEquals(false, currentAuditor.isEmpty());
        Assertions.assertEquals("NOT_HTTP", currentAuditor.get());
    }
}