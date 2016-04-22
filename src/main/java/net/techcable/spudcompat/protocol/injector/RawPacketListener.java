package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import net.md_5.bungee.protocol.DefinedPacket;
import net.techcable.spudcompat.ProtocolVersion;
import net.techcable.spudcompat.protocol.PlayerConnection;
import net.techcable.spudcompat.utils.Either;

import static com.google.common.base.Preconditions.checkNotNull;

public interface RawPacketListener {

    public Result onSend(PlayerConnection connection, RawPacket packet);

    public Result onReceive(PlayerConnection connection, RawPacket packet);

    public Result onSend(PlayerConnection connection, DefinedPacket definedPacket);

    public Result onReceive(PlayerConnection connection, DefinedPacket definedPacket);

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Result {
        private final boolean canceled;
        private final Either<RawPacket, DefinedPacket> result;
        private final ProtocolVersion version;

        public Result(Either<RawPacket, DefinedPacket> result, ProtocolVersion version) {
            this(false, checkNotNull(result, "Null result"), checkNotNull(version, "Null version"));
        }

        public boolean isIgnored() {
            return result == null && !isCanceled();
        }

        public static final Result IGNORED = new Result(null, null);
        public static final Result CANCELLED = new Result(true, null, null);
    }
}
