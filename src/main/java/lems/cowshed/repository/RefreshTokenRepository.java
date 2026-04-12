package lems.cowshed.repository;

import lems.cowshed.config.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public void save(String refresh) {
        long userId = jwtUtil.getUserId(refresh);
        redisTemplate.opsForValue().set(String.valueOf(userId), refresh, Duration.ofDays(1));
    }

    public boolean isNotExists(String refresh) {
        long userId = jwtUtil.getUserId(refresh);
        return !redisTemplate.hasKey(String.valueOf(userId));
    }

    public void delete(String refresh) {
        long userId = jwtUtil.getUserId(refresh);
        redisTemplate.delete(String.valueOf(userId));
    }
}
