package xu.modules.ratelimit;

import lombok.Data;

@Data
public class RateLimitConfig {

    private static final Long DEFAULT_WINDOW_TIME = 6000L;
    private static final Integer DEFAULT_TIMES = -1;


    private Long windowTime = DEFAULT_WINDOW_TIME;
    private Integer times = DEFAULT_TIMES;

    public RateLimitConfig() {
    }

    public RateLimitConfig(Long windowTime, Integer times) {
        this.windowTime = windowTime;
        this.times = times;
    }
}
