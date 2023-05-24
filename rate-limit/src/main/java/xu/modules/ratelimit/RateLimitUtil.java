package xu.modules.ratelimit;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collections;

@Slf4j
public class RateLimitUtil {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> limitRedisScript;

    public RateLimitUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.limitRedisScript = limitRedisScript();
    }

    public <T, E extends Throwable> T execute(Accept<T, E> accept, RateLimitConfig config, String key) {
        return doExecute(accept, config, key);
    }

    @SneakyThrows
    private <T, E extends Throwable> T doExecute(Accept<T, E> accept, RateLimitConfig config, String key) {
        if (config.getTimes() < 0) {
            return accept.accept();
        }
        while (true) {
            Long result = runScript(config, key);
            if (result > 0) {
                long now = System.currentTimeMillis();
                long sleepTime = config.getWindowTime() - (now - result);
                if (now < result) {
                    log.warn("错误数据，当前时间戳{} < 数据时间戳{}", now, result);
                    sleepTime = config.getWindowTime();
                }
                log.info("{}：超限访问，当前时间窗最早元素时间戳为{}，线程睡{}ms", key, result, sleepTime);
                Thread.sleep(sleepTime);
            } else {
                log.info("{}：在{}ms内访问了{}次", key, config.getWindowTime(), Math.abs(result));
                return accept.accept();
            }
        }
    }

    private Long runScript(RateLimitConfig config, String key) {
        return redisTemplate.execute(limitRedisScript,
                Collections.singletonList(key),
                String.valueOf(config.getWindowTime()),
                String.valueOf(config.getTimes()));
    }

    private RedisScript<Long> limitRedisScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/limit_time_window.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
