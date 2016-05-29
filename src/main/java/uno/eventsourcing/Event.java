package uno.eventsourcing;

public interface Event<Aggregate> {

    void apply(Aggregate aggregate);
}
