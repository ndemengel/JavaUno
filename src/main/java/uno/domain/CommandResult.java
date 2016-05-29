package uno.domain;

import lombok.EqualsAndHashCode;
import uno.eventsourcing.Event;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@EqualsAndHashCode
public class CommandResult<E extends Event<?>> {

    private final boolean success;
    private final List<E> events;
    private final String rejectionReason;

    public static <E extends Event<?>> CommandResult<E> rejection(String reason) {
        return new CommandResult<>(false, emptyList(), checkNotNull(reason));
    }

    public static <E extends Event<?>> CommandResult<E> success(E... events) {
        return new CommandResult<>(true, asList(events), null);
    }

    public static <E extends Event<?>> CommandResult<E> success(List<E> events) {
        return new CommandResult<>(true, checkNotNull(events), null);
    }

    private CommandResult(boolean success, List<E> events, String rejectionReason) {
        this.success = success;
        this.events = events;
        this.rejectionReason = rejectionReason;
    }

    public <T> T map(Function<List<E>, T> successMapping, Function<String, T> rejectionMapping) {
        if (success) {
            return successMapping.apply(events);
        }
        return rejectionMapping.apply(rejectionReason);
    }

    public void handle(Consumer<List<E>> successFunction, Consumer<String> rejectionFunction) {
        map(events -> {
            successFunction.accept(events);
            return null;
        }, rejectionReason -> {
            rejectionFunction.accept(rejectionReason);
            return null;
        });
    }
}
