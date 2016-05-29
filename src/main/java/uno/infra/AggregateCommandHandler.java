package uno.infra;

import uno.domain.CommandResult;
import uno.eventsourcing.Event;

import java.util.function.Function;

public interface AggregateCommandHandler<Aggregate, E extends Event<?>> {

    CommandResult<E> executeCommand(Function<Aggregate, CommandResult<E>> command);
}
