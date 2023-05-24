package xu.modules.ratelimit;

@FunctionalInterface
public interface Accept<T, E extends Throwable> {

    T accept() throws E;
}
