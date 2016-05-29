package uno.infra;

import uno.domain.deck.Card;
import uno.domain.deck.Card.Kickback;
import uno.domain.deck.Card.Rank;
import uno.domain.deck.Color;
import uno.domain.deck.Digit;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class CardCode {

    private static final Map<Character, Color> COLOR_CODES = stream(Color.values())
            .collect(toMap(c -> c.name().charAt(0), identity()));

    public static Card cardFromCode(String cardCode) throws CardCodeFormatException {
        if (cardCode.length() < 2) {
            throw new CardCodeFormatException(cardCode);
        }

        char colorCode = cardCode.charAt(0);
        if (!COLOR_CODES.containsKey(colorCode)) {
            throw new CardCodeFormatException(cardCode);
        }

        String maybeDigitOrK = cardCode.substring(1, 2);
        if (maybeDigitOrK.equals("K")) {
            return new Kickback(COLOR_CODES.get(colorCode));
        }

        int number;
        try {
            number = Integer.parseInt(maybeDigitOrK);
        } catch (NumberFormatException e) {
            throw new CardCodeFormatException(cardCode);
        }

        if (number < 0 || number > 9) {
            throw new CardCodeFormatException(cardCode);
        }

        return new Rank(Digit.values()[number], COLOR_CODES.get(colorCode));
    }

    public static String codeForCard(Card card) {
        if (card instanceof Kickback) {
            return ((Kickback) card).color.name().charAt(0) + "K";
        }
        if (card instanceof Rank) {
            Rank rank = (Rank) card;
            return rank.color.name().charAt(0) + Integer.toString(rank.digit.ordinal());
        }
        throw new IllegalArgumentException("Don't know how to encode: " + card);
    }

    public static class CardCodeFormatException extends Exception {
        public CardCodeFormatException(String invalidCardCode) {
            super("Invalid card code: " + invalidCardCode);
        }
    }
}
