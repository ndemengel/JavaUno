package uno.eventsourcing;

import java.util.List;

public interface EventStore<E extends Event> {
    EventStream<E> loadStream(String streamId);

    void appendToStream(String streamId, StoreVersion expectedVersion, List<E> events);
}
