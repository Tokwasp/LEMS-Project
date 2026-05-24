package lems.cowshed.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 테스트에서는 캐시를 비활성화한다.
 * 운영 코드는 단일 키 {@code eventFirstPage::page0}에 캐싱하는데, 통합테스트는 @Transactional로
 * DB만 롤백되고 Redis 값은 남아 테스트 간 stale 캐시가 공유된다(비결정적 실패). NoOpCacheManager로
 * 바꾸면 cache.get()이 항상 null → getEventsPaging이 매번 DB를 조회해 결정적으로 통과한다.
 */
@TestConfiguration
public class TestCacheConfig {

    @Bean
    @Primary
    public CacheManager noOpCacheManager() {
        return new NoOpCacheManager();
    }
}
