package lems.cowshed.config;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void test() {
        String key = "testKey";
        String value = "Hello Upstash!";

        redisTemplate.opsForValue().set(key, value);
        String findValue = (String) redisTemplate.opsForValue().get(key);
        Assertions.assertThat(value).isEqualTo(findValue);
    }
}