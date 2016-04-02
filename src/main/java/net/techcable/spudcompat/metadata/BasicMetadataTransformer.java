package net.techcable.spudcompat.metadata;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import net.techcable.spudcompat.ProtocolVersion;

/**
 * A {@link MetadataTransformer} that does basic transformations (like primitive casting) between metadata
 */
public class BasicMetadataTransformer implements MetadataTransformer {
    private final int[] indexes;
    private final MetadataDataType[] types;

    private BasicMetadataTransformer(int[] indexes, MetadataDataType[] types) {
        this.indexes = indexes;
        this.types = types;
        for (ProtocolVersion version : ProtocolVersion.values()) {
            assert getIndex(version).isPresent() == getDataType(version).isPresent();
        }
    }

    @Override
    public void convert(MetadataMap source, MetadataMap destination) {
        MetadataTransformer.validateConversion(source, destination); // Validate
        ProtocolVersion originalVersion = source.getVersion();
        ProtocolVersion newVersion = destination.getVersion();
        MetadataDataType originalType = getDataType(originalVersion).orElseThrow(() -> new UnsupportedOperationException("Unsupported version: " + originalVersion));
        MetadataDataType newType = getDataType(newVersion).orElseThrow(() -> new UnsupportedOperationException("Unsupported version: " + newVersion));
        int originalIndex = getIndex(originalVersion).orElseThrow(() -> new UnsupportedOperationException("Unsupported version: " + originalVersion));
        int newIndex = getIndex(newVersion).orElseThrow(() -> new UnsupportedOperationException("Unsupported version: " + newVersion));
        assert canConvertBetween(originalType, newType) : "Can't convert between types " + originalType + " and " + newType + " for versions " + originalVersion + " and " + newVersion;
        MetadataDataValue originalValue = source.getValue(originalIndex).orElseThrow(() -> new IllegalStateException("Metadata not present at index: " + originalIndex));
        if (originalValue.getDataType() != originalType) {
            throw new IllegalArgumentException("Unexpected data type at index " + originalIndex + " '" + originalValue.getDataType() + "'. Expected " + originalType);
        }
        MetadataDataValue newValue = convert(originalValue, newType);
        destination.putValue(newIndex, newValue);
    }

    private Optional<MetadataDataType> getDataType(ProtocolVersion version) {
        return Optional.ofNullable(types[version.ordinal()]);
    }

    private OptionalInt getIndex(ProtocolVersion version) {
        int i = indexes[version.ordinal()];
        if (i < 0) return OptionalInt.empty();
        return OptionalInt.of(i);
    }

    public static BasicMetadataTransformer create(int index, ProtocolVersion version1, MetadataDataType type1, ProtocolVersion version2, MetadataDataType type2) {
        return create(index, ImmutableMap.of(version1, type1, version2, type2));
    }

    public static BasicMetadataTransformer create(int index, ImmutableMap<ProtocolVersion, MetadataDataType> types) {
        Preconditions.checkNotNull(types, "Null types");
        ImmutableMap.Builder<ProtocolVersion, Integer> indexes = ImmutableMap.builder();
        types.forEach((version, type) -> indexes.put(version, index));
        return create(indexes.build(), types);
    }

    public static BasicMetadataTransformer create(ImmutableMap<ProtocolVersion, Integer> indexes, MetadataDataType dataType) {
        Preconditions.checkNotNull(indexes, "Null indexes");
        Preconditions.checkNotNull(dataType, "Null dataType");
        ImmutableMap.Builder<ProtocolVersion, MetadataDataType> dataTypes = ImmutableMap.builder();
        indexes.forEach((version, index) -> dataTypes.put(version, dataType));
        return create(indexes, dataTypes.build());
    }

    public static BasicMetadataTransformer create(ImmutableMap<ProtocolVersion, Integer> indexes, ImmutableMap<ProtocolVersion, MetadataDataType> types) {
        Preconditions.checkNotNull(indexes, "Null indexes");
        Preconditions.checkNotNull(types, "Null types");
        Preconditions.checkArgument(types.size() > 1, "Must convert between at least two types, not %s", types.size());
        // Validate that all possible version combinations can be converted between
        for (ProtocolVersion firstVersion : ProtocolVersion.values()) {
            for (ProtocolVersion secondVersion : ProtocolVersion.values()) {
                MetadataDataType firstType = types.get(firstVersion);
                MetadataDataType secondType = types.get(secondVersion);
                if (firstType != null && secondType != null) {
                    if (!canConvertBetween(firstType, secondType)) {
                        throw new IllegalArgumentException("Can't convert between type " + firstType + " for version " + firstVersion + " and type " + secondType + " for version " + secondVersion);
                    }
                }
            }
        }
        indexes.forEach((version, index) -> Preconditions.checkArgument(types.containsKey(version), "Type for version %s not specified, but index (%s) is", version, index));
        MetadataDataType[] typesArray = new MetadataDataType[ProtocolVersion.values().length];
        int[] indexesArray = new int[ProtocolVersion.values().length];
        Arrays.fill(indexesArray, -1);
        types.forEach((version, dataType) -> {
            Preconditions.checkArgument(indexes.containsKey(version), "Index for version %s not specified, byt type (%s) was", version, dataType);
            indexesArray[version.ordinal()] = indexes.get(version);
            typesArray[version.ordinal()] = dataType;
        });
        return new BasicMetadataTransformer(indexesArray, typesArray);
    }

    public static MetadataDataValue convert(MetadataDataValue value, MetadataDataType newType) {
        Preconditions.checkNotNull(value, "Null value");
        if (!canConvertBetween(value.getDataType(), newType)) {
            throw new IllegalArgumentException("Can't convert from " + value.getDataType() + " to " + newType);
        }
        Number num = ((Number) value.getValue());
        switch (newType) {
            case BYTE:
                return MetadataDataValue.ofByte(num.byteValue());
            case SHORT:
                return MetadataDataValue.ofShort(num.shortValue());
            case INT:
                return MetadataDataValue.ofInt(num.intValue());
            case FLOAT:
                return MetadataDataValue.ofFloat(num.floatValue());
            default:
                throw new AssertionError("Check failed between " + value.getDataType() + " and " + newType);
        }
    }

    public static boolean canConvertBetween(MetadataDataType first, MetadataDataType second) {
        Preconditions.checkNotNull(first, "Null first type");
        Preconditions.checkNotNull(second, "Null second type");
        return first.isNumber() && second.isNumber();
    }
}
