package uno.eventsourcing;

import java.util.stream.Stream;

public class EventStream<E extends Event> {

    private final StoreVersion currentVersion;
    private final Stream<E> events;

    public EventStream(StoreVersion currentVersion, Stream<E> events) {
        this.currentVersion = currentVersion;
        this.events = events;
    }

    public StoreVersion currentVersion() {
        return currentVersion;
    }

    public Stream<E> get() {
        return events;
    }
}
