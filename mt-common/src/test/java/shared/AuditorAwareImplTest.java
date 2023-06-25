package shared;

import com.mt.common.infrastructure.audit.SpringDataJpaConfig;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuditorAwareImplTest {

    SpringDataJpaConfig.AuditorAwareImpl auditorAware = new SpringDataJpaConfig.AuditorAwareImpl();

    @Test
    public void getCurrentAuditor_noAuth() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        Assertions.assertFalse(currentAuditor.isEmpty());
        Assertions.assertEquals("NOT_HTTP", currentAuditor.get());
    }
}