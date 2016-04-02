package net.techcable.spudcompat.metadata;

import java.util.Optional;

import com.google.common.base.Preconditions;

import net.techcable.spudcompat.ProtocolVersion;

public interface MetadataMap {

    public ProtocolVersion getVersion();

    public Object getRawValue(int index);

    public default Optional<MetadataDataValue> getValue(int index) {
        Object raw = getRawValue(index);
        return raw == null ? Optional.empty() : Optional.of(MetadataDataValue.ofRawObject(raw));
    }

    public int size();

    public void removeValue(int index);

    public void putValue(int index, MetadataDataValue value);

    public default void putAll(MetadataMap m) {
        Preconditions.checkNotNull(m, "Null metadata");
        Preconditions.checkArgument(m.getVersion() == this.getVersion(), "Version %s doesn't match this version %s", m.getVersion(), this.getVersion());
        for (int i = 0; i < m.size(); i++) {
            final int index = i;
            m.getValue(i).ifPresent((value) -> this.putValue(index, value));
        }
    }

    public default void trimToSize() {}

    /**
     * Return a copy of this metadata map
     * <p>Returned instanced are expected to be of the same type as the current instance.</p>
     *
     * @return a copy of this map
     */
    public MetadataMap copy();

    public default Object[] toArray() {
        Object[] result = new Object[this.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = getRawValue(i);
        }
        return result;
    }

    /**
     * Create a new metadata map with the given version
     * <p>Maps returned by this method are <b>not</b> thread safe.</p>
     *
     * @param version the version of the new map
     * @return a new metadata map
     */
    @SuppressWarnings("deprecation")
    public static MetadataMap create(ProtocolVersion version) {
        return new MetadataMapImpl(version);
    }
}
