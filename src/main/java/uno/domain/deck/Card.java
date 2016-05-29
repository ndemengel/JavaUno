package uno.domain.deck;

import lombok.Value;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Card {

    default boolean changesDirection() {
        return false;
    }

    @Value
    final class Kickback implements Card {
        public final Color color;

        public Kickback(Color color) {
            this.color = checkNotNull(color);
        }

        @Override
        public boolean changesDirection() {
            return true;
        }
    }

    @Value
    final class Rank implements Card {
        public final Digit digit;
        public final Color color;

        public Rank(Digit digit, Color color) {
            this.digit = checkNotNull(digit);
            this.color = checkNotNull(color);
        }
    }
}