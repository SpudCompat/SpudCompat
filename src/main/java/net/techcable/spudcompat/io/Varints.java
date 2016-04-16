package net.techcable.spudcompat.io;

import io.netty.buffer.ByteBuf;

/**
 * Encoders, Decoders, and Utilities for <a href="https://developers.google.com/protocol-buffers/docs/encoding#varints">Protocol Buffer Varints</a>
 *
 * @author Techcable
 */
public class Varints {
    /**
     * If set, indicates there are more bytes to come
     * Otherwise, the byte is the last one.
     */
    public static final int FLAG_BIT = 0x80;
    public static final int VALUE_BITS = 0xFF & ~FLAG_BIT;
    public static final int MAX_SIZE = 5;

    public static int sizeOf(int num) {
        int size = 0;
        do {
            size++;
            num >>>= 7;
        } while (num != 0);
        return size;
    }


    public static void writeVarInt(ByteBuf buf, int num) {
        writeVarInt(buf::writeByte, num);
    }

    public static void writeVarInt(ByteOutput out, int num) {
        while (num >>> 7 != 0) {
            out.writeByte((byte) ((num & VALUE_BITS) | FLAG_BIT));
            num >>>= 7;
        }
        out.writeByte((byte) (num & VALUE_BITS));
    }

    public static int readVarInt(ByteBuf buf) {
        return readVarInt(buf::readByte);
    }

    public static int readVarInt(ByteInput in) {
        int result = 0;
        byte b;
        int size = 0;
        do {
            b = in.readByte();
            int value = (b & VALUE_BITS) << (7 * size++);
            if (size > MAX_SIZE) throw new IllegalArgumentException("Too many bytes");
            result |= value;
        } while ((b & FLAG_BIT) == FLAG_BIT);
        return result;
    }

    public static boolean hasMoreBytes(byte b) {
        return (b & FLAG_BIT) == FLAG_BIT;
    }
}
