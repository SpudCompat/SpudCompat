package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.Packet;
import net.techcable.spudcompat.protocol.PlayerConnection;
import net.techcable.spudcompat.protocol.ProtocolDirection;
import net.techcable.spudcompat.protocol.ProtocolState;

import static com.google.common.base.Preconditions.checkNotNull;

public interface PacketListener {

    public default Result onRawSend(PlayerConnection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    public default Result onRawReceive(PlayerConnection connection, RawPacket packet) {
        return Result.IGNORED;
    }

    public Result onSend(PlayerConnection connection, Packet definedPacket);

    public Result onReceive(PlayerConnection connection, Packet definedPacket);

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Result {
        private final boolean canceled;
        private final RawPacket result;
        private final ProtocolVersion version;

        public Result(RawPacket result, ProtocolVersion version) {
            this(false, checkNotNull(result, "Null result"), checkNotNull(version, "Null version"));
        }

        public Result(Packet packet, ProtocolVersion version, ProtocolState state, ProtocolDirection direction) {
            this(packet.toRaw(checkNotNull(version, "Null version"), checkNotNull(state, "Null state"), checkNotNull(direction, "Null direction")), version);
        }

        public boolean isIgnored() {
            return result == null && !isCanceled();
        }

        public static final Result IGNORED = new Result(null, null);
        public static final Result CANCELLED = new Result(true, null, null);
    }
}
