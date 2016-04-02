package net.techcable.spudcompat.metadata;

import lombok.*;

import java.util.Arrays;

import net.techcable.spudcompat.ProtocolVersion;

import org.apache.commons.lang3.ArrayUtils;

public final class ImmutableMetadataMap implements MetadataMap {
    @Getter
    private final ProtocolVersion version;
    private final Object[] data;

    /* default */ ImmutableMetadataMap(ProtocolVersion version, Object[] data) {
        this.version = version;
        this.data = data;
    }

    public static ImmutableMetadataMap of(ProtocolVersion version) {
        return new ImmutableMetadataMap(version, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    public static ImmutableMetadataMap copyOf(MetadataMap other) {
        return new ImmutableMetadataMap(other.getVersion(), other.toArray());
    }

    @Override
    public Object getRawValue(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Negative index: " + index);
        } else if (index < data.length) {
            return data[index];
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public MetadataMap copy() {
        return this; // Immutable maps need not be copied as they are immutable
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(data, data.length);
    }

    // Unsupported Mutators


    @Override
    public void putAll(MetadataMap m) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public void removeValue(int index) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public void putValue(int index, MetadataDataValue value) {
        throw new UnsupportedOperationException("Immutable");
    }
}
