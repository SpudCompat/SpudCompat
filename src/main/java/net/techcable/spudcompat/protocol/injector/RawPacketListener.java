package net.techcable.spudcompat.protocol.injector;

import lombok.*;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.SlicedByteBuf;
import io.netty.buffer.Unpooled;

import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;
import net.techcable.spudcompat.protocol.Connection;
import net.techcable.spudcompat.utils.Either;

import org.apache.commons.lang3.tuple.Pair;

public interface RawPacketListener {

    public Result onSend(Connection connection, RawPacket packet);

    public Result onReceive(Connection connection, RawPacket packet);

    public Result onSend(Connection connection, DefinedPacket definedPacket);

    public Result onReceive(Connection connection, DefinedPacket definedPacket);

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Result {
        private final boolean canceled;
        private final Either<RawPacket, DefinedPacket> result;

        public Result(Either<RawPacket, DefinedPacket> result) {
            this(false, result);
        }

        public boolean isIgnored() {
            return result != null;
        }

        public static final Result IGNORED = new Result(null);
        public static final Result CANCELLED = new Result(true, null);
    }
}
