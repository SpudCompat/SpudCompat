package net.techcable.spudcompat.protocol;

import lombok.*;

import com.google.common.base.Preconditions;

@RequiredArgsConstructor
@Getter
public enum PacketType {
    ;

    private final int id;
    private final ProtocolState state;
    private final ProtocolDirection direction;

    public static PacketType get(int id, ProtocolState state, ProtocolDirection direction) {
        Preconditions.checkArgument(id >= 0, "Negative id %s", id);
        Preconditions.checkNotNull(state, "Null state");
        Preconditions.checkNotNull(direction, "Null direction");
        return null;
    }
}
