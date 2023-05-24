package xu.modules.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RateLimitAutoConfiguration {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Bean
    public RateLimitUtil rateLimitUtil(StringRedisTemplate redisTemplate) {
        return new RateLimitUtil(redisTemplate);
    }
}
