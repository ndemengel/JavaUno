package uno.domain;

import lombok.EqualsAndHashCode;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@EqualsAndHashCode
public final class GameId {
    private final String id;

    public GameId(String id) {
        this.id = checkNotNull(id);
    }

    public GameId() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return id;
    }
}
