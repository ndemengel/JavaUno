package uno.infra;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.EqualsAndHashCode;
import uno.eventsourcing.Event;
import uno.eventsourcing.EventStore;
import uno.eventsourcing.EventStream;
import uno.eventsourcing.StoreVersion;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class InMemoryEventStore<E extends Event> implements EventStore<E> {

    private final Multimap<String, E> streams = ArrayListMultimap.create();

    @Override
    public EventStream<E> loadStream(String streamId) {
        Collection<E> events = streams.get(streamId);
        return new EventStream<>(new Version(events.size()), events.stream());
    }

    @Override
    public void appendToStream(String streamId, StoreVersion expectedVersion, List<E> newEvents) {
        checkArgument(expectedVersion instanceof Version);
        Collection<E> serializedEvents = streams.get(streamId);
        // will do for now
        checkState(((Version) expectedVersion).offset == serializedEvents.size(), "Unexpected version");

        serializedEvents.addAll(newEvents);
    }

    @EqualsAndHashCode
    private static class Version implements StoreVersion {
        final int offset;

        Version(int offset) {
            this.offset = offset;
        }
    }
}
