package uno.domain.event;

import uno.domain.Game;
import uno.domain.GameId;

public class GameOver extends GameEvent {
    protected GameOver(GameId gameId) {
        super(gameId);
    }

    @Override
    public void apply(Game game) {
        // TODO not implemented for now
    }
}
