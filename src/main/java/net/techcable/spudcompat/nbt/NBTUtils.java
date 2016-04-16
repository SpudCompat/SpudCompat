package net.techcable.spudcompat.nbt;

import lombok.*;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import io.netty.buffer.ByteBuf;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NBTUtils {
    /**
     * Calculate the length of the NBT tag, without actually reading it.
     * <p>
     * This is <i>not always</i> an O(1) operation.
     * Calculating the length of compound tags is O(n),
     * Calculating the length of primitives, primitive arrays, and strings is O(1),
     * Calculating the length of lists is O(n), unless its a list of primitives.
     * However it uses very little memory.</p>
     *
     * @param buf the buf to ca
     * @return the number of bytes that are in the nbt tag
     * @throws IllegalArgumentException if the NBT is invalid
     */
    public static int calculateNBTLength(ByteBuf buf) {
        buf = buf.duplicate(); // Don't mess with whatever they were doing with indexes
        int oldIndex = buf.readerIndex();
        buf.markReaderIndex();
        validateReadable(buf, 1, "type id");
        byte typeId = buf.readByte();
        skipTag(buf, typeId);
        int size = buf.readerIndex() - oldIndex;
        buf.resetReaderIndex();
        return size;
    }

    private static final int[] PRIMITIVE_SIZES = new int[]{0, 1, 2, 4, 8, 4, 8};
    private static final String[] PRIMITIVE_NAMES = new String[]{"end", "byte", "short", "integer", "long", "float", "double"};

    private static void skipTag(ByteBuf buf, final byte typeId) {
        switch (typeId) {
            case 0: // end tag
                break;
            case 1: // Byte
            case 2: // Short
            case 3: // integer
            case 4: // long
            case 5: // float
            case 6: // double
                int primitiveSize = PRIMITIVE_SIZES[typeId];
                if (primitiveSize < 0) throw new AssertionError("Negative amount: " + primitiveSize);
                if (buf.readerIndex() > buf.writerIndex() - primitiveSize) {
                    throw new IllegalArgumentException("Not enough bytes for " + PRIMITIVE_NAMES[typeId]);
                }
                buf.skipBytes(primitiveSize);
                break;
            case 7: // byte array
                validateReadable(buf, 4, "byte array length");
                int length = buf.readInt();
                validateReadable(buf, length, "byte array");
                buf.skipBytes(length);
                break;
            case 8: // String
                skipString(buf);
                break;
            case 9: // list
                skipList(buf);
                break;
            case 10:
                skipCompoundTag(buf);
                break;
            case 11:
                validateReadable(buf, 4, "int array length");
                length = buf.readInt() * 4;
                validateReadable(buf, length, "int array");
                buf.skipBytes(length);
            default:
                throw new IllegalArgumentException("Unknown type id: " + typeId);
        }
    }

    private static void skipList(ByteBuf buf) {
        validateReadable(buf, 1, "list element type id");
        byte typeId = buf.readByte();
        validateReadable(buf, 4, "list length");
        int length = buf.readInt();
        if (typeId < 0) {
            throw new IllegalArgumentException("Negative type id");
        } else if (typeId < PRIMITIVE_SIZES.length) {
            // The length of the type is constant, and therefore we can simply skip lengthOfList * lengthOfType
            int typeSizes = PRIMITIVE_SIZES[typeId];
            int toSkip = typeSizes * length;
            validateReadable(buf, toSkip, "list");
            buf.skipBytes(toSkip);
        } else {
            for (int i = 0; i < length; i++) {
                skipTag(buf, typeId);
            }
        }
    }

    private static void skipCompoundTag(ByteBuf buf) {
        while (true) {
            if (!buf.isReadable()) {
                throw new IllegalArgumentException("Compound tag not ended");
            }
            byte typeId = buf.readByte();
            if (typeId == 0) {
                return; // End tag
            } else {
                skipTag(buf, typeId);
            }
        }
    }

    private static void skipString(ByteBuf buf) {
        validateReadable(buf, 2, "string length");
        int length = buf.readUnsignedShort();
        validateReadable(buf, length, "string");
        buf.skipBytes(buf.readUnsignedShort());
    }

    private static void validateReadable(ByteBuf buf, int amount, String name) {
        if (amount < 0) {
            throw new AssertionError("Negative amount: " + amount);
        }
        if (buf.readerIndex() > buf.writerIndex() - amount) {
            throw new IllegalArgumentException("Not enough bytes for " + name);
        }
    }
}