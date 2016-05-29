package uno.infra;

import uno.domain.CommandResult;
import uno.eventsourcing.Event;
import uno.eventsourcing.EventStore;
import uno.eventsourcing.EventStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class CommandHandler<AggregateId, Aggregate, AggregateEvent extends Event<Aggregate>> {

    private final Constructor<Aggregate> aggregateConstructor;
    private final EventStore<AggregateEvent> eventStore;
    private final String streamPrefix;

    public CommandHandler(Class<Aggregate> aggregateType, String streamPrefix, EventStore<AggregateEvent> eventStore) {
        try {
            aggregateConstructor = aggregateType.getConstructor(Optional.class, Stream.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        this.eventStore = eventStore;
        this.streamPrefix = streamPrefix;
    }

    public CommandResult<AggregateEvent> loadAggregateAndExecute(AggregateId aggregateId, Function<Aggregate, CommandResult<AggregateEvent>> command) {
        String streamId = streamPrefix + "-" + aggregateId;
        EventStream<AggregateEvent> stream = eventStore.loadStream(streamId);

        Aggregate aggregate = buildAggregate(stream.get());
        CommandResult<AggregateEvent> result = command.apply(aggregate);

        result.handle(
                newEvents -> eventStore.appendToStream(streamId, stream.currentVersion(), newEvents),
                rejectionReason -> System.out.println("[DEBUG] " + rejectionReason)
        );

        return result;
    }

    private Aggregate buildAggregate(Stream<AggregateEvent> events) {
        try {
            return aggregateConstructor.newInstance(Optional.empty(), events);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public AggregateCommandHandler<Aggregate, AggregateEvent> forAggregateId(AggregateId aggregateId) {
        return command -> loadAggregateAndExecute(aggregateId, command);
    }
}
