package net.techcable.spudcompat.metadata;

import lombok.*;

import com.google.common.base.Preconditions;

import net.techcable.spudcompat.BlockPos;

@RequiredArgsConstructor
public enum MetadataDataType {
    BYTE(Byte.class),
    SHORT(Short.class),
    INT(Integer.class),
    FLOAT(Float.class),
    STRING(String.class),
    STACK(null),
    BLOCK_POS(BlockPos.class);

    private final Class<?> clazz;

    public final Object cast(Object o) {
        if (!getClazz().isInstance(o)) {
            throw new IllegalArgumentException("Value " + o + " doesn't match type " + this);
        }
        return o;
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(getClazz());
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object convertTo(MetadataDataType to, Object value) {
        if (to == STACK) throw new UnsupportedOperationException("Stacks are unsupported");
        return new UnsupportedOperationException("Can't convert " + value + " from " + this + " to " + to);
    }

    public static MetadataDataType typeOf(Object o) {
        Preconditions.checkNotNull(o, "Null object");
        return typeOf(o.getClass());
    }

    public static MetadataDataType typeOf(Class<?> c) {
        Preconditions.checkNotNull(c, "Null class");
        Preconditions.checkArgument(!c.isPrimitive(), "Primitive class: %s", c);
        if (Number.class.isAssignableFrom(c)) {
            if (c == Byte.class) {
                return BYTE;
            } else if (c == Short.class) {
                return SHORT;
            } else if (c == Integer.class) {
                return INT;
            } else if (c == Float.class) {
                return FLOAT;
            }
        } else if (c == String.class) {
            return STRING;
        } else if (c == BlockPos.class) {
            return BLOCK_POS;
        }
        throw new IllegalArgumentException("Unknown metadata class " + c);
    }
}
