package uno.domain;

import lombok.Value;

import static com.google.common.base.Preconditions.checkArgument;

@Value
public final class Player {

    public final int number;

    public Player(int number) {
        checkArgument(number >= 0, "Player number must be positive");
        this.number = number;
    }
}
