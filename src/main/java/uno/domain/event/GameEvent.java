package uno.domain.event;

import lombok.EqualsAndHashCode;
import uno.domain.Game;
import uno.domain.GameId;
import uno.eventsourcing.Event;

import static com.google.common.base.Preconditions.checkNotNull;

@EqualsAndHashCode
public abstract class GameEvent implements Event<Game> {

    public final GameId gameId;

    protected GameEvent(GameId gameId) {
        this.gameId = checkNotNull(gameId);
    }
}
