package uno.domain.event;

import lombok.EqualsAndHashCode;
import lombok.Value;
import uno.domain.Direction;
import uno.domain.Game;
import uno.domain.GameId;
import uno.domain.deck.Card;

import static com.google.common.base.Preconditions.checkNotNull;

@Value
@EqualsAndHashCode(callSuper = true)
public class GameStarted extends GameEvent {

    public final int playerCount;
    public final Card firstCard;
    public final Direction direction;

    public GameStarted(GameId gameId, int playerCount, Card firstCard, Direction direction) {
        super(gameId);
        this.playerCount = playerCount;
        this.firstCard = checkNotNull(firstCard);
        this.direction = checkNotNull(direction);
    }

    @Override
    public void apply(Game game) {
        game.handle(this);
    }
}
