package net.techcable.spudcompat.metadata;

import com.google.common.collect.ImmutableSet;

import net.techcable.spudcompat.ProtocolVersion;

public interface MetadataTransformer {
    /**
     * Convert the metadata in the first map, to the metadata in the second map
     * <p>Does not modify the source map.</p>
     *
     * @param source      the source to fetch metadata from
     * @param destination the destination to put the resulting metadata in
     * @throws IllegalArgumentException if the source map and destination map have the same version
     * @throws IllegalArgumentException an immutable map is passed as the destination
     * @throws IllegalArgumentException if any of the source metadata is invalid for the given version]\
     * @throws IllegalStateException if the metadata isn't present
     * @throws NullPointerException if either of the maps are null
     * @throws UnsupportedOperationException if the destination or source versions are unsupported.
     */
    public void convert(MetadataMap source, MetadataMap destination);

    public default void initializeDefaults(MetadataMap map) {}

    public static void validateConversion(MetadataMap source, MetadataMap destination) {
        if (source == null) {
            throw new NullPointerException("Null source");
        } else if (destination == null) {
            throw new NullPointerException("Null destination");
        } else if (source.getVersion() == destination.getVersion()) {
            throw new IllegalArgumentException("Trying to convert to a map of the same version: " + source.getVersion());
        } else if (destination instanceof ImmutableMetadataMap) {
            throw new IllegalArgumentException("Gave an immutable map as destination, which can't be modified");
        }
    }
}
