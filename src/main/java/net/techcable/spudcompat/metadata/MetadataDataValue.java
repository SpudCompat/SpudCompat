package net.techcable.spudcompat.metadata;

import lombok.*;

import com.google.common.base.Preconditions;

import net.techcable.spudcompat.BlockPos;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class MetadataDataValue {
    private final Object value;
    private final MetadataDataType dataType;


    public static MetadataDataValue ofByte(byte b) {
        return new MetadataDataValue(b, MetadataDataType.BYTE);
    }

    public static MetadataDataValue ofShort(short s) {
        return new MetadataDataValue(s, MetadataDataType.SHORT);
    }

    public static MetadataDataValue ofInt(int i) {
        return new MetadataDataValue(i, MetadataDataType.INT);
    }


    public static MetadataDataValue ofFloat(float f) {
        return new MetadataDataValue(f, MetadataDataType.FLOAT);
    }

    public static MetadataDataValue ofString(String s) {
        return new MetadataDataValue(s, MetadataDataType.STRING);
    }

    public static MetadataDataValue ofBlockPos(BlockPos pos) {
        return new MetadataDataValue(pos, MetadataDataType.BLOCK_POS);
    }


    public static MetadataDataValue ofRawObject(Object o) {
        Preconditions.checkNotNull(o, "Null object");
        if (o instanceof Number) {
            if (o instanceof Byte) {
                return ofByte(((Byte) o));
            } else if (o instanceof Short) {
                return ofShort(((Short) o));
            } else if (o instanceof Integer) {
                return ofInt(((Integer) o));
            } else if (o instanceof Float) {
                return ofFloat(((Float) o));
            }
        } else if (o instanceof String) {
            return ofString(((String) o));
        } else if (o instanceof BlockPos) {
            return ofBlockPos(((BlockPos) o));
        }
        throw new IllegalArgumentException("Unknown metadata class: " + o.getClass());
    }
}
