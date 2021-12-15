package shared;

import com.mt.common.infrastructure.audit.SpringDataJpaConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class AuditorAwareImplTest {

    SpringDataJpaConfig.AuditorAwareImpl auditorAware = new SpringDataJpaConfig.AuditorAwareImpl();

    @Test
    public void getCurrentAuditor_noAuth() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        Assert.assertEquals(false, currentAuditor.isEmpty());
        Assert.assertEquals("NOT_HTTP", currentAuditor.get());
    }
}