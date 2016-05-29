package uno.domain;

import lombok.EqualsAndHashCode;
import uno.doc.Aggregate;
import uno.domain.deck.Card;
import uno.domain.event.GameEvent;
import uno.domain.event.GameStarted;

import java.util.Optional;
import java.util.stream.Stream;

@Aggregate
public final class Game {

    private final State state;

    public Game(Optional<State> snapshot, Stream<GameEvent> events) {
        state = snapshot.orElse(new State());
        events.forEach(e -> e.apply(this));
    }

    public State createSnapshot() {
        return state;
    }

    public CommandResult startGame(GameId gameId, int playerCount, Card firstCard) {
        if (state.started) {
            return CommandResult.rejection("Game already started");
        }
        if (playerCount <= 1) {
            return CommandResult.rejection("Can't play Uno with less than two players");
        }

        Direction startDirection = firstCard.changesDirection() ? Direction.COUNTERCLOCKWISE : Direction.CLOCKWISE;
        return CommandResult.success(new GameStarted(gameId, playerCount, firstCard, startDirection));
    }

    public CommandResult playCard(GameId gameId, Player player, Card card) {
        // TODO
        return CommandResult.rejection("Not implemented yet");
    }

    public void handle(GameStarted gameStarted) {
        state.started = true;
        state.playerCount = gameStarted.playerCount;
        state.currentCard = gameStarted.firstCard;
        state.direction = gameStarted.direction;
    }

    @EqualsAndHashCode
    public static class State {
        // no access from the outside
        private Card currentCard;
        private Player currentPlayer;
        private Direction direction;
        private int playerCount;
        private boolean started;

        private State() {
        }
    }
}
