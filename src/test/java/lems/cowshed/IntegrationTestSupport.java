package lems.cowshed;

import lems.cowshed.config.TestCacheConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@Import(TestCacheConfig.class)
@SpringBootTest
public abstract class IntegrationTestSupport {
}
