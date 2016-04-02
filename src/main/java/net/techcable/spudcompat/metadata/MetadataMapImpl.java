package net.techcable.spudcompat.metadata;

import lombok.*;

import java.util.Arrays;

import com.google.common.base.Preconditions;

import net.techcable.spudcompat.ProtocolVersion;

import org.apache.commons.lang3.ArrayUtils;

/**
 * The implementation of {@link MetadataMap}, based on automatically expanding arrays.
 * Not thread safe
 *
 */
/* default */ class MetadataMapImpl implements MetadataMap {
    @Getter
    private final ProtocolVersion version;
    private Object[] data;
    private int size = 0;

    /* default */ MetadataMapImpl(ProtocolVersion version) {
        this(version, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    private MetadataMapImpl(ProtocolVersion version, Object[] data) {
        Preconditions.checkNotNull(version, "Null version");
        Preconditions.checkNotNull(data, "Null data");
        this.version = version;
        this.data = data;
    }

    @Override
    public Object getRawValue(int index) {
        if (index < 0) throw new IllegalArgumentException("Negative index: " + index);
        return index < size() ? data[index] : null;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public void removeValue(int index) {
        Preconditions.checkArgument(index >= 0, "Negative index: %s", index);
        if (index < data.length) {
            data[index] = null;
        }
    }

    @Override
    public void putValue(int index, MetadataDataValue value) {
        setValue(index, value.getValue());
    }

    private void setValue(int index, Object value) {
        Preconditions.checkArgument(index >= 0, "Negative index: %s", index);
        Preconditions.checkNotNull(value, "Null value");
        expandToFit(index);
        data[index] = value;
        if (index >= size) {
            size = index + 1;
        }
    }

    private void expandToFit(int index) {
        assert index >= 0 : "Negative index " + index;
        if (data.length >= index) {
            data = Arrays.copyOf(data, index + 10);
        }
    }

    public void trimToSize() {
        if (data.length == size) return;
        this.data = Arrays.copyOf(data, size);
    }

    @Override
    public MetadataMap copy() {
        MetadataMapImpl copy = new MetadataMapImpl(this.getVersion());
        copy.data = Arrays.copyOf(this.data, this.size);
        return copy;
    }

    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.data, this.size);
    }
}
