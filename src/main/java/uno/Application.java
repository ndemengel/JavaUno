package uno;

import com.google.common.collect.Iterables;
import uno.domain.CommandResult;
import uno.domain.Game;
import uno.domain.GameId;
import uno.domain.Player;
import uno.domain.deck.Card;
import uno.domain.deck.Color;
import uno.domain.deck.Digit;
import uno.domain.event.GameEvent;
import uno.domain.event.GameOver;
import uno.infra.AggregateCommandHandler;
import uno.infra.CardCode;
import uno.infra.CommandHandler;
import uno.infra.InMemoryEventStore;

import java.util.List;
import java.util.Scanner;

import static uno.infra.CardCode.cardFromCode;
import static uno.infra.CardCode.codeForCard;

public class Application {

    public static void main(String... args) {
        // TODO add snapshot support
        CommandHandler<GameId, Game, GameEvent> commandHandler = new CommandHandler<>(Game.class, "game", new InMemoryEventStore<>());

        GameId gameId = new GameId();
        startGame(gameId, commandHandler.forAggregateId(gameId));
    }

    private static void startGame(GameId gameId, AggregateCommandHandler<Game, GameEvent> gameHandler) {
        Card firstCard = randomFirstCard();
        int playerCount = 3;

        gameHandler.executeCommand(game -> game.startGame(gameId, playerCount, firstCard));

        // sample input/output handling
        Scanner scanner = new Scanner(System.in);
        System.out.println("Game started with 3 players: 0, 1 and 2. First card is: " + codeForCard(firstCard));
        System.out.println();

        while (true) {
            Player player = askForPlayer(playerCount, scanner);
            Card card = askForCard(player, scanner);

            CommandResult<GameEvent> result = gameHandler.executeCommand(game -> game.playCard(gameId, player, card));

            result.handle(
                    events -> {
                        System.out.println("\tPlayer A played " + card);

                        if (gameIsOver(events)) {
                            System.out.println("GAME OVER");
                            System.exit(0);
                        }
                    },
                    rejectionReason -> System.err.println("\tCard rejected: " + rejectionReason)
            );
        }
    }

    private static boolean gameIsOver(List<GameEvent> newEvents) {
        return !newEvents.isEmpty() && Iterables.getLast(newEvents) instanceof GameOver;
    }

    private static Player askForPlayer(int playerCount, Scanner scanner) {
        Player player = null;
        while (player == null) {
            System.out.print("* Who's the next player? ");

            if (!scanner.hasNext()) {
                System.out.println("\nBye\n");
                System.exit(0);
            }

            String playerNumberStr = scanner.next();
            try {
                int number = Integer.parseInt(playerNumberStr);
                if (number >= playerCount) {
                    System.err.println("Invalid player number: " + number);
                } else {
                    player = new Player(number);
                }
            } catch (NumberFormatException e) {
                System.err.println("\t" + e.getMessage());
            }
        }
        return player;
    }

    private static Card askForCard(Player player, Scanner scanner) {
        Card card = null;
        while (card == null) {
            System.out.print("* Player " + player.number + ", play a card: ");

            if (!scanner.hasNext()) {
                System.out.println("\nBye\n");
                System.exit(0);
            }

            String cardCode = scanner.next();
            try {
                card = cardFromCode(cardCode);
            } catch (CardCode.CardCodeFormatException e) {
                System.err.println("\t" + e.getMessage());
            }
        }
        return card;
    }

    private static Card randomFirstCard() {
        Digit randomDigit = Digit.values()[(int) Math.floor(Math.random() * Digit.values().length)];
        Color color = Color.values()[(int) Math.floor(Math.random() * Color.values().length)];
        return new Card.Rank(randomDigit, color);
    }
}
