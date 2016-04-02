package net.techcable.spudcompat.metadata;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.techcable.spudcompat.ProtocolVersion;

public class DefaultingMetadataTransformer implements MetadataTransformer {

    private final int[] indexes;
    private final MetadataDataValue[] defaults;

    private DefaultingMetadataTransformer(int[] indexes, MetadataDataValue[] defaults) {
        this.indexes = indexes;
        this.defaults = defaults;
        for (ProtocolVersion version : ProtocolVersion.values()) {
            assert getIndex(version).isPresent() == getDefaultValue(version).isPresent();
        }
    }

    @Override
    public void convert(MetadataMap source, MetadataMap destination) {} // Do nothing, aka ignore metadata

    @Override
    public void initializeDefaults(MetadataMap map) {
        ProtocolVersion version = map.getVersion();
        getDefaultValue(version).ifPresent((defaultValue) -> {
            int index = getIndex(version).getAsInt();
            map.putValue(index, defaultValue);
        });
    }

    public Optional<MetadataDataValue> getDefaultValue(ProtocolVersion version) {
        return Optional.ofNullable(defaults[version.ordinal()]);
    }

    private OptionalInt getIndex(ProtocolVersion version) {
        int i = indexes[version.ordinal()];
        if (i < 0) return OptionalInt.empty();
        return OptionalInt.of(i);
    }

    public static DefaultingMetadataTransformer create(int index, ProtocolVersion version, MetadataDataValue defaultValue) {
        return create(index, ImmutableMap.of(version, defaultValue));
    }

    public static DefaultingMetadataTransformer create(int index, ImmutableMap<ProtocolVersion, MetadataDataValue> defaults) {
        Preconditions.checkNotNull(defaults, "Null defaults");
        Preconditions.checkArgument(!defaults.isEmpty(), "Empty defaults");
        MetadataDataValue[] defaultsArray = new MetadataDataValue[ProtocolVersion.values().length];
        int[] indexes = new int[ProtocolVersion.values().length];
        Arrays.fill(indexes, -1);
        defaults.forEach((supportedVersion, dataType) -> {
            indexes[supportedVersion.ordinal()] = index;
            defaultsArray[supportedVersion.ordinal()] = dataType;
        });
        return new DefaultingMetadataTransformer(indexes, defaultsArray);
    }
}
